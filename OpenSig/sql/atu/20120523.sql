
# tabela produtos compostos por mais de um produto
CREATE TABLE `prod_composicao` (
  `prod_composicao_id` int(11) NOT NULL AUTO_INCREMENT,
  `prod_produto_principal` int(11) NOT NULL,
  `prod_produto_id` int(11) NOT NULL,
  `prod_embalagem_id` int(11) NOT NULL,
  `prod_composicao_quantidade` decimal(10,4) NOT NULL,
  `prod_composicao_valor` decimal(10,2) NOT NULL,
  PRIMARY KEY (`prod_composicao_id`),
  KEY `FK_prod_composicao_1` (`prod_produto_principal`) USING BTREE,
  KEY `FK_prod_composicao_2` (`prod_produto_id`) USING BTREE,
  KEY `FK_prod_composicao_3` (`prod_embalagem_id`) USING BTREE,
  CONSTRAINT `FK_prod_composicao_1` FOREIGN KEY (`prod_produto_principal`) REFERENCES `prod_produto` (`prod_produto_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_prod_composicao_2` FOREIGN KEY (`prod_produto_id`) REFERENCES `prod_produto` (`prod_produto_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_prod_composicao_3` FOREIGN KEY (`prod_embalagem_id`) REFERENCES `prod_embalagem` (`prod_embalagem_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

