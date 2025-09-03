//package co.com.crediya.solicitudes.consumer.config;
//
//import com.squareup.okhttp.mockwebserver.MockResponse;
//import com.squareup.okhttp.mockwebserver.MockWebServer;
//import org.junit.jupiter.api.Test;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//class RestConsumerConfigTest {
//
//    @Test
//    void debeConectarseAlServidorMockYRecibir200() throws Exception {
//        try (MockWebServer server = new MockWebServer()) {
//            // Enqueue una respuesta simple para verificar conectividad
//            server.enqueue(new MockResponse()
//                    .setResponseCode(200)
//                    .setHeader("Content-Type", "application/json")
//                    .setBody("\"ok\""));
//
//            server.start();
//            String baseUrl = server.url("/").toString();
//
//            // Instanciar el config con la URL del servidor mock y timeout de 5s
//            RestConsumerConfig config = new RestConsumerConfig(baseUrl, 5000);
//            WebClient client = config.getWebClient(WebClient.builder());
//
//            // Ejecutar una llamada GET simple
//            String body = client.get()
//                    .uri("/ping")
//                    .retrieve()
//                    .bodyToMono(String.class)
//                    .block();
//
//            assertEquals("ok", body);
//        }
//    }
//}

