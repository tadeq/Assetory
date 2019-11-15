package pl.edu.agh.assetory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.assetory.model.client.ComputerInformation;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ComputerInformationService {
    private RestHighLevelClient client;

    private ObjectMapper objectMapper;

    @Autowired
    public ComputerInformationService(RestHighLevelClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public void putMappings() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("computer_info");
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                builder.startObject("id");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();
                builder.startObject("computerId");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();
                builder.startObject("date");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();
            }
            builder.endObject();
        }
        builder.endObject();
        request.mapping(builder);
        client.indices().create(request, RequestOptions.DEFAULT);
    }

    public ComputerInformation addComputerInformation(ComputerInformation information) throws IOException {
        IndexRequest request = new IndexRequest("computer_info");
        Map<String, Object> documentMapper = objectMapper.convertValue(information, Map.class);
        request.source(documentMapper, XContentType.JSON);
        client.index(request, RequestOptions.DEFAULT);
        return information;
    }

    public List<ComputerInformation> getAllComputerInformation() throws IOException {
        SearchRequest searchRequest = new SearchRequest("computer_info");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery()).size(10000);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);
    }

    public List<ComputerInformation> findByComputerId(String computerId) throws IOException {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery(ComputerInformation.COMPUTER_ID_FIELD, computerId));
        SearchRequest searchRequest = new SearchRequest("computer_info").source(new SearchSourceBuilder().query(queryBuilder).size(10000));
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);
    }

    public Optional<ComputerInformation> findByComputerIdAndDate(String computerId, String date) throws IOException {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery(ComputerInformation.COMPUTER_ID_FIELD, computerId))
                .must(QueryBuilders.termQuery(ComputerInformation.DATE_FIELD, date));
        SearchRequest searchRequest = new SearchRequest("computer_info").source(new SearchSourceBuilder().query(queryBuilder).size(10000));
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse).stream().findFirst();
    }

    private List<ComputerInformation> getSearchResult(SearchResponse response) {
        SearchHit[] searchHit = response.getHits().getHits();
        List<ComputerInformation> computerInformation = Lists.newArrayList();
        if (searchHit.length > 0) {
            Arrays.stream(searchHit)
                    .forEach(hit -> computerInformation.add(objectMapper.convertValue(hit.getSourceAsMap(), ComputerInformation.class)));
        }
        return computerInformation;
    }
}
