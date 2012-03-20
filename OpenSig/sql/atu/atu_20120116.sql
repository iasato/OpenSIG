# bloco 1010 nao necessario
UPDATE `fis_sped_bloco` SET `fis_sped_bloco_classe` = NULL WHERE `fis_sped_bloco_id`='179';
DELETE FROM sis_configuracao WHERE sis_configuracao_chave LIKE 'SPED.1010%';
# adicionando bloco 0500
UPDATE `fis_sped_bloco` SET `fis_sped_bloco_classe`='br.com.opensig.fiscal.server.sped.bloco0.Registro0500' WHERE `fis_sped_bloco_id`='18';