package pl.edu.agh.assetory.utils;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Value("${elasticsearch.host}")
    private String elasticsearchHost;

    @Value("${elasticsearch.username}")
    private String username;

    @Value("${elasticsearch.password}")
    private String password;


    @Bean(destroyMethod = "close")
    RestHighLevelClient client() {

        if (elasticsearchHost.equals("")) {
            HttpHost localhost = new HttpHost("localhost", 9200);
            return new RestHighLevelClient(RestClient.builder(localhost));
        } else {
            final CredentialsProvider credentialsProvider =
                    new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(username, password));
            return new RestHighLevelClient(
                    RestClient.builder(new HttpHost(elasticsearchHost))
                            .setHttpClientConfigCallback(httpClientBuilder ->
                                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)));
        }
    }
}