# remove a obrigacao de ter uma descricao, pois tendo o produto_id nao precisa
ALTER TABLE `com_ecf_venda_produto` CHANGE COLUMN `com_ecf_venda_produto_descricao` `com_ecf_venda_produto_descricao` VARCHAR(100) NULL  ;
