package com.usian.service;
import com.github.pagehelper.PageHelper;
import com.usian.mapper.SearchMapper;
import com.usian.pojo.SearchItem;
import com.usian.utils.JsonUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SearchServiceImp implements SearchService {

    //注入配置文件中定义的索引库，和索引表名
    @Value("${ES_INDEX_NAME}")
    private String ES_INDEX_NAME;

    @Value("${ES_TYPE_NAME}")
    private String ES_TYPE_NAME;
    
    @Autowired   //注入自定mapper查询的sql语句
    private SearchMapper searchMapper;

    @Autowired   //注入es的工具
    private RestHighLevelClient restHighLevelClient;

    @Override
    public Boolean importAll(){
        try {
            if(!isExistsIndex()){   //判断查询索引库的结果，如果没有该索引库，
                createIndex();      //调用创建索引库的方法
            }
            int page=1;
            while (true){
                PageHelper.startPage(page,1000);    //导入到第一页，每页导入1000条数据
                List<SearchItem> searchItemList = searchMapper.getItemList(); //查询数据
                if (searchItemList==null || searchItemList.size()==0){   //判断是否查到数据
                    break;     //当查到的数据为空的话结束循环
                }

                BulkRequest bulkRequest = new BulkRequest();  //创建批量添加对象
                for (SearchItem searchItem : searchItemList) {
                    bulkRequest.add(new IndexRequest(ES_INDEX_NAME,ES_TYPE_NAME).source(JsonUtils.objectToJson(searchItem),XContentType.JSON));  //添加
                }
                restHighLevelClient.bulk(bulkRequest,RequestOptions.DEFAULT);  //发送批量添加请求
                page++;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //以关键字分页查询商品信息
    @Override
    public List<SearchItem> selectByQ(String q, long page, Integer pageSize) {
        try {
            SearchRequest searchRequest = new SearchRequest(ES_INDEX_NAME);  //创建索引搜索请求，告知搜索的索引库
            searchRequest.types(ES_TYPE_NAME);                              //告知搜索的索引类型表

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();    //创建以关键字搜索的条件对象，分装关键字和以关键字要查询的字段
            searchSourceBuilder.query(QueryBuilders.multiMatchQuery(q,new String[]{"item_title","item_sell_point","item_category_name","item_desc"}));  //分装关键字，和字段

            /**
             * 分页 ：第几页    从第几条开始查询   每页几条数据   推出公式
             *          1           0                  20        (1-1)*pageSize
             *          2           20                 20        (2-1)*pageSize
             *          3           40                 20        (3-1)*pageSize
             */
            Long from =(page-1)*pageSize;
            searchSourceBuilder.from(from.intValue()); //分装页码，第几页
            searchSourceBuilder.size(pageSize);        //分装每页显示几条数据；

            HighlightBuilder highlightBuilder = new HighlightBuilder();  //创建高亮条件对象，分装条件
            highlightBuilder.preTags("<font color='red'>");
            highlightBuilder.postTags("</font>");
            highlightBuilder.field("item_title"); //分装高亮字段

            searchSourceBuilder.highlighter(highlightBuilder);  //分装高亮条件
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT); //发送请求得到相应对象
            SearchHit[] hits = searchResponse.getHits().getHits();  //通过相应对象 得到数据参数

           List<SearchItem> searchItemList = new ArrayList<>();     //前台要的是集合创建集合分装结果
            for (SearchHit hit : hits) {  //循环数据数组，
                SearchItem searchItem = JsonUtils.jsonToPojo(hit.getSourceAsString(), SearchItem.class);  //将每条数据分装成对象

                Map<String, HighlightField> highlightFields = hit.getHighlightFields();   //获取高亮参数集合
                if (highlightFields!=null && highlightFields.size()>0){                   //判断是否有高亮的参数值
                    HighlightField item_title = highlightFields.get("item_title");        //过去高亮字段
                    Text[] fragments = item_title.getFragments();                         //获取高亮字段里面的高亮值数组
                    searchItem.setItem_title(fragments[0].toString());                    //将高亮值从新赋值给对象参数
                }
                searchItemList.add(searchItem);   //将最后的结果分装到集合中响应给前台
            }
            return searchItemList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //添加商品同步到索引库
    @Override
    public int addDocement(String itemID) throws IOException {
            //1、根据ID 查询出来新添加的商品
            SearchItem searchItem = searchMapper.addDocement(itemID);
            System.out.println(searchItem);

            IndexRequest indexRequest = new IndexRequest(ES_INDEX_NAME);  //创建添加索引的请求
            indexRequest.type(ES_TYPE_NAME);
            indexRequest.source(JsonUtils.objectToJson(searchItem),XContentType.JSON);
            IndexResponse index = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);  //发送添加索引请求
            return index.getShardInfo().getFailed();  //返回
    }

    //定义一个查询导入数据的时候索引库是否存在
    public boolean isExistsIndex() throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest();    //创建获取索引请求
        getIndexRequest.indices(ES_INDEX_NAME);    //分装要查询那个索引到请求中
        return restHighLevelClient.indices().exists(getIndexRequest,RequestOptions.DEFAULT);
    }

    //创建索引库方法
    public void createIndex() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(ES_INDEX_NAME);  //创建 创建索引，告诉创建的索引名称
        createIndexRequest.settings(Settings.builder().put("number_of_shards",2).put("number_of_replicas",1)); //设置主分片，和副分片
        //mapping添加字段
        createIndexRequest.mapping(ES_TYPE_NAME,"{\n" +
                "  \"_source\": {\n" +
                "    \"excludes\":[\"item_desc\"]\n" +
                "  }, \n" +
                "  \"properties\": {\n" +
                "    \"id\":{\n" +
                "      \"type\": \"keyword\"\n" +
                "    },\n" +
                "    \"item_title\":{\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\"\n" +
                "    },\n" +
                "    \"item_sell_point\":{\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\"\n" +
                "    },\n" +
                "    \"item_price\":{\n" +
                "      \"type\": \"float\"\n" +
                "    },\n" +
                "    \"item_image\":{\n" +
                "      \"type\": \"text\",\n" +
                "      \"index\": false\n" +
                "    },\n" +
                "    \"item_category_name\":{\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\"\n" +
                "    },\n" +
                "    \"item_desc\":{\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\"\n" +
                "    }\n" +
                "  }\n" +
                "}\n",XContentType.JSON);
        restHighLevelClient.indices().create(createIndexRequest,RequestOptions.DEFAULT);
    }
}
