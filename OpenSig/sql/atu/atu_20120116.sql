/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- bloco 1010 nao necessario
UPDATE `fis_sped_bloco` SET `fis_sped_bloco_classe` = NULL WHERE `fis_sped_bloco_id`='179';
DELETE FROM sis_configuracao WHERE sis_configuracao_chave LIKE 'SPED.1010%';

-- adicionando bloco 0500
UPDATE `fis_sped_bloco` SET `fis_sped_bloco_classe`='br.com.opensig.fiscal.server.sped.bloco0.Registro0500' WHERE `fis_sped_bloco_id`='18';

# removendo uma configuracao
DELETE FROM sis_configuracao WHERE sis_configuracao_chave = 'NFE.TIPOOPER';

-- criando nova tabela de tipo de produto
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

-- colocando a acao de NFe de Entrada e Inutilzando
UPDATE `sis_acao` SET `sis_acao_classe`='br.com.opensig.comercial.client.controlador.comando.acao.ComandoGerarNfeSaida' WHERE `sis_acao_classe`='br.com.opensig.comercial.client.controlador.comando.acao.ComandoGerarNfe';
INSERT INTO `sis_acao` (`sis_funcao_id`, `sis_acao_classe`, `sis_acao_ordem`, `sis_acao_subordem`, `sis_acao_ativo`, `sis_acao_visivel`) VALUES (20, 'br.com.opensig.comercial.client.controlador.comando.acao.ComandoGerarNfeEntrada', 13, 0, 1, 1);
UPDATE `sis_acao` SET `sis_acao_classe`='br.com.opensig.fiscal.client.controlador.comando.acao.ComandoInutilizarSaida' WHERE `sis_acao_classe`='br.com.opensig.fiscal.client.controlador.comando.acao.ComandoInutilizar';
INSERT INTO `sis_acao` (`sis_funcao_id`, `sis_acao_classe`, `sis_acao_ordem`, `sis_acao_subordem`, `sis_acao_ativo`, `sis_acao_visivel`) VALUES (50, 'br.com.opensig.fiscal.client.controlador.comando.acao.ComandoInutilizarEntrada', 1, 0, 1, 1);
UPDATE `sis_acao` SET `sis_acao_ordem`=16 WHERE `sis_acao_classe`='br.com.opensig.fiscal.client.controlador.comando.acao.ComandoValidar';

-- lembrar de dar permissao aos grupos ou usuarios.

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;