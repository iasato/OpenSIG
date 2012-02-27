DELETE FROM sis_permissao WHERE sis_acao_id > 0 AND NOT sis_acao_id IN (SELECT sis_acao_id FROM sis_acao);
DELETE FROM sis_permissao WHERE sis_funcao_id > 0 AND NOT sis_funcao_id IN (SELECT sis_funcao_id FROM sis_funcao);
DELETE FROM sis_permissao WHERE sis_modulo_id > 0 AND NOT sis_modulo_id IN (SELECT sis_modulo_id FROM sis_modulo);

ALTER TABLE `com_ecf_z` CHANGE COLUMN `com_ecf_z_data` `com_ecf_z_data` DATE NOT NULL  ;
ALTER TABLE `com_ecf_venda_produto` CHANGE COLUMN `com_ecf_venda_produto_codigo` `com_ecf_venda_produto_codigo` VARCHAR(14) NULL  ;

CREATE  TABLE `fis_sped_fiscal` (
  `fis_sped_fiscal_id` INT NOT NULL AUTO_INCREMENT ,
  `emp_empresa_id` INT NOT NULL ,
  `fis_sped_fiscal_ano` INT NOT NULL ,
  `fis_sped_fiscal_mes` INT NOT NULL ,
  `fis_sped_fiscal_tipo` VARCHAR(20) NOT NULL ,
  `fis_sped_fiscal_data` DATE NOT NULL ,
  `fis_sped_fiscal_compras` INT NOT NULL ,
  `fis_sped_fiscal_frete` INT NOT NULL ,
  `fis_sped_fiscal_vendas` INT NOT NULL ,
  `fis_sped_fiscal_ecf` INT NOT NULL ,
  `fis_sped_fiscal_ativo` TINYINT(1) NOT NULL ,
  `fis_sped_fiscal_protocolo` VARCHAR(15) NOT NULL ,
  PRIMARY KEY (`fis_sped_fiscal_id`) ,
  UNIQUE INDEX `UNIQUE` USING BTREE (`emp_empresa_id` ASC, `fis_sped_fiscal_ano` ASC, `fis_sped_fiscal_mes` ASC, `fis_sped_fiscal_tipo` ASC) ,
  INDEX `FK_fis_sped_fiscal_1` USING BTREE (`emp_empresa_id` ASC) ,
  CONSTRAINT `FK_fis_sped_fiscal_1`
    FOREIGN KEY (`emp_empresa_id` )
    REFERENCES `emp_empresa` (`emp_empresa_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

ALTER TABLE `prod_produto` CHANGE COLUMN `prod_produto_barra` `prod_produto_barra` VARCHAR(14) NULL  , CHANGE COLUMN `prod_produto_sinc` `prod_produto_sinc` INT(11) NOT NULL  ;
ALTER TABLE `prod_preco` CHANGE COLUMN `prod_preco_barra` `prod_preco_barra` VARCHAR(14) NULL  ;

CREATE  TABLE `prod_ipi` (
  `prod_ipi_id` INT NOT NULL AUTO_INCREMENT ,
  `prod_ipi_nome` VARCHAR(100) NOT NULL ,
  `prod_ipi_cst_entrada` VARCHAR(2) NOT NULL ,
  `prod_ipi_cst_saida` VARCHAR(2) NOT NULL ,
  `prod_ipi_aliquota` DECIMAL(4,2) NOT NULL ,
  `prod_ipi_decreto` VARCHAR(1000) NOT NULL ,
  PRIMARY KEY (`prod_ipi_id`) )
ENGINE = InnoDB;

INSERT INTO `prod_ipi` (`prod_ipi_id`, `prod_ipi_nome`, `prod_ipi_cst_entrada`, `prod_ipi_cst_saida`, `prod_ipi_aliquota`, `prod_ipi_decreto`) VALUES (1, 'Entrada / Saida Tributada', '00', '50', 0, ' ');
INSERT INTO `prod_ipi` (`prod_ipi_id`, `prod_ipi_nome`, `prod_ipi_cst_entrada`, `prod_ipi_cst_saida`, `prod_ipi_aliquota`, `prod_ipi_decreto`) VALUES (2, 'Entrada / Saida tributada com alíquota zero', '01', '51', 0, ' ');
INSERT INTO `prod_ipi` (`prod_ipi_id`, `prod_ipi_nome`, `prod_ipi_cst_entrada`, `prod_ipi_cst_saida`, `prod_ipi_aliquota`, `prod_ipi_decreto`) VALUES (3, 'Entrada / Saida isenta', '02', '52', 0, ' ');
INSERT INTO `prod_ipi` (`prod_ipi_id`, `prod_ipi_nome`, `prod_ipi_cst_entrada`, `prod_ipi_cst_saida`, `prod_ipi_aliquota`, `prod_ipi_decreto`) VALUES (4, 'Entrada / Saida não-tributada', '03', '53', 0, ' ');
INSERT INTO `prod_ipi` (`prod_ipi_id`, `prod_ipi_nome`, `prod_ipi_cst_entrada`, `prod_ipi_cst_saida`, `prod_ipi_aliquota`, `prod_ipi_decreto`) VALUES (5, 'Entrada / Saida imune', '04', '54', 0, ' ');
INSERT INTO `prod_ipi` (`prod_ipi_id`, `prod_ipi_nome`, `prod_ipi_cst_entrada`, `prod_ipi_cst_saida`, `prod_ipi_aliquota`, `prod_ipi_decreto`) VALUES (6, 'Entrada / Saida com suspensão', '05', '55', 0, ' ');
INSERT INTO `prod_ipi` (`prod_ipi_id`, `prod_ipi_nome`, `prod_ipi_cst_entrada`, `prod_ipi_cst_saida`, `prod_ipi_aliquota`, `prod_ipi_decreto`) VALUES (7, 'Outras entradas / saidas', '49', '99', 0, ' ');

UPDATE prod_ipi SET prod_ipi_nome = UPPER(prod_ipi_nome);
UPDATE prod_ipi SET prod_ipi_decreto = "";

ALTER TABLE `prod_produto` ADD COLUMN `prod_ipi_id` INT(11) NOT NULL  AFTER `prod_tributacao_id`;
UPDATE `prod_produto` SET `prod_ipi_id` = 7;
ALTER TABLE `prod_produto`  
  ADD CONSTRAINT `FK_prod_produto_6`
  FOREIGN KEY (`prod_ipi_id` )
  REFERENCES `prod_ipi` (`prod_ipi_id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION
, ADD INDEX `FK_prod_produto_6` USING BTREE (`prod_ipi_id` ASC) ;

