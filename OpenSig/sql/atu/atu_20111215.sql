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

CREATE  TABLE `fis_sped_bloco` (
  `fis_sped_bloco_id` INT NOT NULL AUTO_INCREMENT ,
  `fis_sped_bloco_icms_ipi` TINYINT(1) NOT NULL ,
  `fis_sped_bloco_pis_cofins` TINYINT(1) NOT NULL ,
  `fis_sped_bloco_letra` VARCHAR(1) NOT NULL ,
  `fis_sped_bloco_descricao` VARCHAR(500) NOT NULL ,
  `fis_sped_bloco_registro` VARCHAR(4) NOT NULL ,
  `fis_sped_bloco_obrigatorio` TINYINT(1) NOT NULL ,
  `fis_sped_bloco_classe` VARCHAR(255) NULL ,
  `fis_sped_bloco_ordem` INT NOT NULL ,
  PRIMARY KEY (`fis_sped_bloco_id`) )
ENGINE = InnoDB;

CREATE  TABLE `fis_sped_bloco_empresa` (
  `fis_sped_bloco_empresa_id` INT NOT NULL AUTO_INCREMENT ,
  `emp_empresa_id` INT NOT NULL ,
  `fis_sped_bloco_id` INT NOT NULL ,
  PRIMARY KEY (`fis_sped_bloco_empresa_id`) ,
  INDEX `FK_fis_sped_bloco_empresa_1` USING BTREE (`emp_empresa_id` ASC) ,
  INDEX `FK_fis_sped_bloco_empresa_2` USING BTREE (`fis_sped_bloco_id` ASC) ,
  CONSTRAINT `FK_fis_sped_bloco_empresa_1`
    FOREIGN KEY (`emp_empresa_id` )
    REFERENCES `emp_empresa` (`emp_empresa_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `FK_fis_sped_bloco_empresa_2`
    FOREIGN KEY (`fis_sped_bloco_id` )
    REFERENCES `fis_sped_bloco` (`fis_sped_bloco_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

INSERT INTO `sis_funcao` (`sis_modulo_id`, `sis_funcao_classe`, `sis_funcao_ordem`, `sis_funcao_subordem`, `sis_funcao_ativo`) VALUES (6, 'br.com.opensig.fiscal.client.controlador.comando.ComandoSpedFiscal', 7, 0, 1);

INSERT INTO `com_natureza` (`emp_empresa_id`, `com_natureza_nome`, `com_natureza_descricao`, `com_natureza_cfop_trib`, `com_natureza_cfop_sub`, `com_natureza_icms`, `com_natureza_ipi`, `com_natureza_pis`, `com_natureza_cofins`) VALUES (1, 'COMPRA', 'COMPRA DE MERCADORIAS', 2102, 2403, 0, 0, 0, 0);
