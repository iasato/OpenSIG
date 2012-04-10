# remove a obrigacao de ter uma descricao, pois tendo o produto_id nao precisa
ALTER TABLE `com_ecf_venda_produto` CHANGE COLUMN `com_ecf_venda_produto_descricao` `com_ecf_venda_produto_descricao` VARCHAR(100) NULL;

# adicionando as aliq e removendo os marcadores para os campos de PIS e COFINS
ALTER TABLE `com_natureza` CHANGE COLUMN `com_natureza_pis` `com_natureza_pis` DECIMAL(4,2) NOT NULL  , CHANGE COLUMN `com_natureza_cofins` `com_natureza_cofins` DECIMAL(4,2) NOT NULL  ;

UPDATE com_natureza, sis_configuracao SET com_natureza_pis = sis_configuracao_valor WHERE com_natureza_pis > 0 AND com_natureza.emp_empresa_id = sis_configuracao.emp_empresa_id AND sis_configuracao_chave = 'NFE.PIS';
DELETE FROM sis_configuracao WHERE sis_configuracao_chave = 'NFE.PIS';

UPDATE com_natureza, sis_configuracao SET com_natureza_cofins = sis_configuracao_valor WHERE com_natureza_cofins > 0 AND com_natureza.emp_empresa_id = sis_configuracao.emp_empresa_id AND sis_configuracao_chave = 'NFE.COFINS';
DELETE FROM sis_configuracao WHERE sis_configuracao_chave = 'NFE.COFINS';

# adicioando o campo de enquadramento do IPI
ALTER TABLE `prod_ipi` ADD COLUMN `prod_ipi_enq` VARCHAR(3) NOT NULL  AFTER `prod_ipi_aliquota` ;

UPDATE prod_ipi, sis_configuracao SET prod_ipi_enq = sis_configuracao_valor WHERE  sis_configuracao_chave = 'NFE.IPI_ENQ';
DELETE FROM sis_configuracao WHERE sis_configuracao_chave = 'NFE.IPI';
DELETE FROM sis_configuracao WHERE sis_configuracao_chave = 'NFE.IPI_ENQ';