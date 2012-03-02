# total dos produtos da compra
update com_compra set com_compra_valor_produto = (select sum(com_compra_produto_total) from com_compra_produto where com_compra_produto.com_compra_id = com_compra.com_compra_id)
where com_compra_valor_produto <> (select sum(com_compra_produto_total) from com_compra_produto where com_compra_produto.com_compra_id = com_compra.com_compra_id); 

select com_compra.com_compra_id,com_compra_valor_produto,com_compra_observacao
from com_compra where com_compra_valor_produto <> (select sum(com_compra_produto_total) from com_compra_produto where com_compra_produto.com_compra_id = com_compra.com_compra_id); 

# total base icms da compra
update com_compra set com_compra_icms_base = (select sum(com_compra_produto_total) from com_compra_produto where com_compra_produto.com_compra_id = com_compra.com_compra_id and com_compra_produto_icms > 0)
where com_compra_icms_base <> (select sum(com_compra_produto_total) from com_compra_produto where com_compra_produto.com_compra_id = com_compra.com_compra_id and com_compra_produto_icms > 0); 

select com_compra.com_compra_id,com_compra_icms_base,com_compra_observacao
from com_compra where com_compra_icms_base <> (select sum(com_compra_produto_total) from com_compra_produto where com_compra_produto.com_compra_id = com_compra.com_compra_id and com_compra_produto_icms > 0); 

# total de icms da compra
update com_compra set com_compra_icms_valor = (select round(sum(com_compra_produto_total * com_compra_produto_icms / 100),2) from com_compra_produto where com_compra_produto.com_compra_id = com_compra.com_compra_id and com_compra_produto_icms > 0)
where com_compra_icms_valor <> (select round(sum(com_compra_produto_total * com_compra_produto_icms / 100),2) from com_compra_produto where com_compra_produto.com_compra_id = com_compra.com_compra_id and com_compra_produto_icms > 0); 

select com_compra.com_compra_id,com_compra_icms_valor,com_compra_observacao
from com_compra where com_compra_icms_valor <> (select round(sum(com_compra_produto_total * com_compra_produto_icms / 100),2) from com_compra_produto where com_compra_produto.com_compra_id = com_compra.com_compra_id and com_compra_produto_icms > 0); 

# tota de ipi da compra
update com_compra set com_compra_valor_ipi = (select round(sum(com_compra_produto_total * com_compra_produto_ipi / 100),2) from com_compra_produto where com_compra_produto.com_compra_id = com_compra.com_compra_id and com_compra_produto_ipi > 0)
where com_compra_valor_ipi <> (select round(sum(com_compra_produto_total * com_compra_produto_ipi / 100),2) from com_compra_produto where com_compra_produto.com_compra_id = com_compra.com_compra_id and com_compra_produto_ipi > 0); 

select com_compra.com_compra_id,com_compra_valor_ipi,com_compra_observacao
from com_compra where com_compra_valor_ipi <> (select round(sum(com_compra_produto_total * com_compra_produto_ipi / 100),2) from com_compra_produto where com_compra_produto.com_compra_id = com_compra.com_compra_id and com_compra_produto_ipi > 0); 

# total da nota da compra
update com_compra set com_compra_valor_nota = (com_compra_valor_produto+com_compra_valor_seguro+com_compra_valor_ipi+com_compra_valor_outros-com_compra_valor_desconto)
where com_compra_valor_nota <> (com_compra_valor_produto+com_compra_valor_seguro+com_compra_valor_ipi+com_compra_valor_outros-com_compra_valor_desconto);

select com_compra.com_compra_id,com_compra_valor_nota,com_compra_observacao
from com_compra where com_compra_valor_nota <> (com_compra_valor_produto+com_compra_valor_seguro+com_compra_valor_ipi+com_compra_valor_outros-com_compra_valor_desconto);

# arrumar cfop da compra
update com_compra_produto set com_compra_produto_cfop = com_compra_produto_cfop - 4000  where com_compra_produto_cfop >= 5000;

select distinct com_compra_produto_cfop, count(com_compra_produto_cfop) from com_compra_produto where com_compra_produto_cfop >= 5000
group by com_compra_produto_cfop;

# arrumar cfop da frete
update com_frete set com_frete_cfop = com_frete_cfop - 4000  where com_frete_cfop >= 5000;

select distinct com_frete_cfop, count(com_frete_cfop) from com_frete where com_frete_cfop >= 5000
group by com_frete_cfop;

# distribuir desconto da venda pra produtos
update com_ecf_venda_produto,com_ecf_venda set com_ecf_venda_produto_desconto = com_ecf_venda_desconto
where com_ecf_venda_produto.com_ecf_venda_id = com_ecf_venda.com_ecf_venda_id;

# aplicar desconto nos produtos
update com_ecf_venda_produto set com_ecf_venda_produto_liquido = com_ecf_venda_produto_bruto - (com_ecf_venda_produto_bruto * com_ecf_venda_produto_desconto / 100)
where com_ecf_venda_produto_desconto > 0;

# aplicar total nos produtos
update com_ecf_venda_produto set com_ecf_venda_produto_total = com_ecf_venda_produto_liquido * com_ecf_venda_produto_quantidade;

# colocar codigo NCM de grupo padrao
update prod_produto set prod_produto_ncm = '95' where prod_produto_ncm = '' or prod_produto_ncm = '00' or (length(prod_produto_ncm) > 2 and length(prod_produto_ncm) < 8);

select prod_produto_ncm, count(prod_produto_ncm) from prod_produto where prod_produto_ncm = '' or prod_produto_ncm = '00' or (length(prod_produto_ncm) > 2 and length(prod_produto_ncm) < 8)
group by prod_produto_ncm;

