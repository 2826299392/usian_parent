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
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

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
            if(!isExistsIndex()){   //判断查询索引库的结果，如果没有该索引库，调用创建索引库的方法
                createIndex();
            }

            int page=1;
            while (true){
                PageHelper.startPage(page,1000);    //导入到第一页，每页导入1000条数据

                List<SearchItem> searchItemList = searchMapper.getItemList(); //查询数据
                if (searchItemList==null || searchItemList.size()==0){
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

    //定义一个查询导入数据的时候索引库是否存在
    public boolean isExistsIndex() throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest();
        getIndexRequest.indices(ES_INDEX_NAME);
        return restHighLevelClient.indices().exists(getIndexRequest,RequestOptions.DEFAULT);
    }

    //创建索引库方法
    public void createIndex() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(ES_INDEX_NAME);
        createIndexRequest.settings(Settings.builder().put("number_of_shards",2).put("number_of_replicas",1));
        createIndexRequest.mapping(ES_TYPE_NAME,"{\n" +
                "  \"properties\": {\n" +
                "    \"item_id\":{\n" +
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
                "}",XContentType.JSON);
        restHighLevelClient.indices().create(createIndexRequest,RequestOptions.DEFAULT);
    }
}
