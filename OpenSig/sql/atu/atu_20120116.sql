# bloco 1010 nao necessario
UPDATE `fis_sped_bloco` SET `fis_sped_bloco_classe` = NULL WHERE `fis_sped_bloco_id`='179';
DELETE FROM sis_configuracao WHERE sis_configuracao_chave LIKE 'SPED.1010%';

# adicionando bloco 0500
UPDATE `fis_sped_bloco` SET `fis_sped_bloco_classe`='br.com.opensig.fiscal.server.sped.bloco0.Registro0500' WHERE `fis_sped_bloco_id`='18';

# removendo uma configuracao
DELETE FROM sis_configuracao WHERE sis_configuracao_chave = 'NFE.TIPOOPER';

# criando nova tabela de tipo de produto
CREATE  TABLE `prod_tipo` (
  `prod_tipo_id` INT NOT NULL AUTO_INCREMENT ,
  `prod_tipo_valor` VARCHAR(2) NOT NULL ,
  `prod_tipo_descricao` VARCHAR(100) NOT NULL ,
  PRIMARY KEY (`prod_tipo_id`) );

INSERT INTO `prod_tipo` (`prod_tipo_id`,`prod_tipo_valor`, `prod_tipo_descricao`) VALUES (1,'00', 'Mercadoria para Revenda');
INSERT INTO `prod_tipo` (`prod_tipo_id`,`prod_tipo_valor`, `prod_tipo_descricao`) VALUES (2,'01', 'Matéria-Prima');
INSERT INTO `prod_tipo` (`prod_tipo_id`,`prod_tipo_valor`, `prod_tipo_descricao`) VALUES (3,'02', 'Embalagem');
INSERT INTO `prod_tipo` (`prod_tipo_id`,`prod_tipo_valor`, `prod_tipo_descricao`) VALUES (4,'03', 'Produto em Processo');
INSERT INTO `prod_tipo` (`prod_tipo_id`,`prod_tipo_valor`, `prod_tipo_descricao`) VALUES (5,'04', 'Produto Acabado');
INSERT INTO `prod_tipo` (`prod_tipo_id`,`prod_tipo_valor`, `prod_tipo_descricao`) VALUES (6,'05', 'Subproduto');
INSERT INTO `prod_tipo` (`prod_tipo_id`,`prod_tipo_valor`, `prod_tipo_descricao`) VALUES (7,'06', 'Produto Intermediário');
INSERT INTO `prod_tipo` (`prod_tipo_id`,`prod_tipo_valor`, `prod_tipo_descricao`) VALUES (8,'07', 'Material de Uso e Consumo');
INSERT INTO `prod_tipo` (`prod_tipo_id`,`prod_tipo_valor`, `prod_tipo_descricao`) VALUES (9,'08', 'Ativo Imobilizado');
INSERT INTO `prod_tipo` (`prod_tipo_id`,`prod_tipo_valor`, `prod_tipo_descricao`) VALUES (10,'09', 'Serviços');
INSERT INTO `prod_tipo` (`prod_tipo_id`,`prod_tipo_valor`, `prod_tipo_descricao`) VALUES (11,'10', 'Outros insumos');
INSERT INTO `prod_tipo` (`prod_tipo_id`,`prod_tipo_valor`, `prod_tipo_descricao`) VALUES (12,'99', 'Outros');
UPDATE `prod_tipo` SET `prod_tipo_descricao` = UCASE(`prod_tipo_descricao`);

ALTER TABLE `prod_produto` ADD COLUMN `prod_tipo_id` INT NOT NULL  AFTER `prod_origem_id` ;
UPDATE `prod_produto` SET `prod_tipo_id` = 1;
ALTER TABLE `prod_produto` 
  ADD CONSTRAINT `FK_prod_produto_7`
  FOREIGN KEY (`prod_tipo_id` )
  REFERENCES `prod_tipo` (`prod_tipo_id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION
, ADD INDEX `FK_prod_produto_7` USING BTREE (`prod_tipo_id` ASC) ;

INSERT INTO `sis_funcao` (`sis_modulo_id`, `sis_funcao_classe`, `sis_funcao_ordem`, `sis_funcao_subordem`, `sis_funcao_ativo`) VALUES (4, 'br.com.opensig.produto.client.controlador.comando.ComandoTipo', 8, 0, 1);
# lembrar de dar permissao aos grupos ou usuarios.
