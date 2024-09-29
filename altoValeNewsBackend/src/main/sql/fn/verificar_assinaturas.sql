CREATE OR REPLACE FUNCTION verificar_assinaturas() 
RETURNS TABLE(usuarios_atualizados bigint, assinaturas_desativadas bigint) AS $$
DECLARE
usuarios_count bigint;
    assinaturas_count bigint;
BEGIN
    -- Atualiza usuários com assinaturas vencidas
WITH usuarios_atualizados AS (
UPDATE usuario u
SET tipo = 0
    FROM assinatura a
WHERE u.user_id = a.assinatura_id
  AND u.tipo = 1
  AND a.vencimento < CURRENT_DATE
  AND a.ativo = true
    RETURNING u.user_id
    )
SELECT COUNT(*) INTO usuarios_count FROM usuarios_atualizados;

-- Atualiza assinaturas vencidas
WITH assinaturas_desativadas AS (
UPDATE assinatura
SET ativo = false
WHERE vencimento < CURRENT_DATE
  AND ativo = true
    RETURNING assinatura_id
    )
SELECT COUNT(*) INTO assinaturas_count FROM assinaturas_desativadas;

-- Registra o log da execução
INSERT INTO log_verificacao_assinaturas (data_execucao, usuarios_atualizados, assinaturas_desativadas)
VALUES (CURRENT_TIMESTAMP, usuarios_count, assinaturas_count);

RETURN QUERY SELECT usuarios_count, assinaturas_count;
END;
$$ LANGUAGE plpgsql;