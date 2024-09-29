package com.ajmv.altoValeNewsBackend.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class AssinaturaVerificador {

    private static final Logger logger = LoggerFactory.getLogger(AssinaturaVerificador.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //Comentar esta linha abaixo durante o teste no Postman
    @Scheduled(cron = "0 0 0 * * ?") // Executa todos os dias à meia-noite
    public void executarVerificacaoAssinaturas() {
        logger.info("Iniciando verificação diária de assinaturas");

        try {
            Map<String, Object> result = jdbcTemplate.queryForMap("SELECT * FROM verificar_assinaturas()");
            long usuariosAtualizados = ((Number) result.get("usuarios_atualizados")).longValue();
            long assinaturasDesativadas = ((Number) result.get("assinaturas_desativadas")).longValue();

            logger.info("Verificação de assinaturas concluída com sucesso. " +
                            "Usuários atualizados: {}. Assinaturas desativadas: {}. " +
                            "Os detalhes foram registrados na tabela log_verificacao_assinaturas.",
                    usuariosAtualizados, assinaturasDesativadas);
        } catch (Exception e) {
            logger.error("Erro ao executar a verificação de assinaturas", e);
        }
    }

    public void executarVerificacaoManual(){
        executarVerificacaoAssinaturas();
    }
}