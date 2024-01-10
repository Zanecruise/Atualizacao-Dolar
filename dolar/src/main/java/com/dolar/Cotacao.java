package com.dolar; // Package do projeto

import org.json.JSONObject; // Bibliotecas utilizadas
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpringBootApplication
@EnableScheduling
public class Cotacao {

    private static final Logger logger = LogManager.getLogger(Cotacao.class); // Necessário para utilização do logger

    private static final String urlProjetoCotacaoDolar = "http://localhost:5000"; // Decidi atribuir a url em uma constante para futuras atualizações
    private static final String endpointDolarAtual = urlProjetoCotacaoDolar + "/dolar_atual"; // Decidi atribuir o endpoint em uma variavel para caso futuramente precise trocar a rota

    public static void main(String[] args) { // Inicialização do Spring Boot
        SpringApplication.run(Cotacao.class, args);
    }

    @Scheduled(fixedDelay = 1800000) // Tempo em que o programa faz a requisição do valor do dolar, na tarefa está como 30 minutos que é igual a 1800000 Milissegundos
    public void obterCotacaoDolar() {

        RestTemplate restTemplate = new RestTemplate();

        try {

            String cotacaoDolarJson = restTemplate.getForObject(endpointDolarAtual, String.class);

            if (cotacaoDolarJson != null) {  // Prefiro fazer a verificação se o valor for nulo no início do que tratar um NullPointerException no catch

                JSONObject dollarRate = new JSONObject(cotacaoDolarJson).getJSONObject("dollar_rate"); // Acesso o objeto
                double bidValue = Double.parseDouble(dollarRate.getString("bid")); // Pego o atributo "bid" do objeto e converto para double, como o valor já vem tratado da API não será necessário atribuir BigDecimal a ele

                // Formato o valor para exibir apenas duas casas decimais
                String formattedValue = String.format("%.2f", bidValue); // Prefiro deixar em String para caso eu exiba em algum documento HTML, mas poderia ser feita uma conversão para utilizar o BigDecimal para fazer iterações com o valor


                logger.info("Valor do Dolar: $" + formattedValue); // Exibe no terminal o valor do dólar

            } else {

                logger.error("Não foi possível obter a cotação do dólar."); // Caso o valor for nulo, exibira a menssagem de erro
            }

        } catch (ResourceAccessException e) {

            logger.error("Erro ao obter a cotacao do dolar. Tente novamente mais tarde.", e); // Erro para caso o programa não consiga acessar o endpoint, exibo a mensagem de erro para fins de depuração mas pode ser removido
            
        } catch (Exception e) {

            logger.error("Erro ao obter a cotacao do dolar.", e); // Erro para uma exceção genérica
            
        }
    }
}
