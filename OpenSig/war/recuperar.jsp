<%@page import="java.util.Date"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
	String url = request.getContextPath() + "/CoreService?data=" + new Date().getTime();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<style>
html,body {
	background: #C4C4C4;
	margin: 0;
	padding: 0;
	margin-left:0px; margin-top:0px;
	font-family: "Lucida Grande", "Lucida Sans Unicode", Verdana, Arial,
		Helvetica, sans-serif;
	font-size: 12px;
}

p,h1,form,button {
	border: 0px;
	margin: 0px;
	padding: 0px;
}

.spacer {
	clear: both;
	height: 1px;
}

/* ----------- My Form ----------- */
.myform {
	width: 520px;
	height: 250px;
	padding: 14px;
	position: absolute;
	left: 50%;
	top: 50%;
	margin-left: -267px;
	margin-top: -132px;
}

/* ----------- stylized ----------- */
#stylized {
	border: solid 2px #b7ddf2;
	background: #ebf4fb;
}

#stylized h1 {
	font-size: 14px;
	font-weight: bold;
	margin-bottom: 8px;
}

#stylized p {
	font-size: 11px;
	color: #666666;
	margin-bottom: 20px;
	border-bottom: solid 1px #b7ddf2;
	padding-bottom: 10px;
}

#stylized label {
	display: block;
	font-weight: bold;
	text-align: right;
	width: 100px;
	float: left;
}

#stylized .small {
	color: #666666;
	display: block;
	font-size: 11px;
	font-weight: normal;
	text-align: right;
	width: 100px;
}

#stylized input {
	float: left;
	font-size: 12px;
	padding: 4px 2px;
	border: solid 1px #b7ddf2;
	width: 400px;
	margin: 2px 0 20px 10px;
}

#stylized img {
	float: left;
	font-size: 12px;
	padding: 4px 2px;
	margin: 2px 0 20px 10px;
}

#stylized button {
	clear: both;
	width: 125px;
	height: 31px;
	text-align: center;
	background: #666666 no-repeat;
	color: #FFFFFF;
	font-size: 12px;
	font-weight: bold;
	cursor: pointer;
}
</style>


<script type="text/javascript">
	function validar(opcao){
		var strChave = document.nfe.chave.value;
		var strCaptcha = document.nfe.captcha.value;
		
		if(strChave == "" || strChave.length < 44)
		{
			alert( "Preencha a chave com 44 digitos!" );
			document.nfe.chave.focus();
		}
		else if(strCaptcha == "")
		{
			alert( "Preencha o código da imagem!" );
			document.nfe.captcha.focus();
		}
		else {
			var url = "<%=request.getContextPath() + "/FiscalService"%>?chave="+ strChave + "&captcha=" + strCaptcha + "&opcao=" + opcao;
			document.getElementById("nfeFrame").src = url;
		}
	}
</script>

<title>OpenSIG - Sistemas Integrados Gerenciáveis Open Source</title>
</head>
<body onload="javascript:document.nfe.chave.focus();">
	<div id="stylized" class="myform">
		<form id="nfe" name="nfe" method="post" action="OpenSigNfe.jsp">
			<a href="http://opensig.com.br" title="OpenSIG"><img
				src="img/logo.png" width="32px" height="32px" /> </a>
			<h1>Baixar Nota Fiscal Eletrônica</h1>
			<p>Este formulário &eacute; usado para baixar os arquivos da NFe.</p>

			<label>Chave <span class="small">Digite a chave</span> </label> <input
				type="text" name="chave" id="chave" maxlength="44" /> <label>Segurança
				<span class="small">Veja a imagem</span> </label> <img src="<%=url%>" /> <label>Código
				<span class="small">Digite o código</span> </label> <input type="text"
				name="captcha" id="captcha" maxlength="5" class="small" />

			<div class="spacer"></div>
			<button type="button" style="margin-left: 50px;"
				onclick="validar(2);">NFe</button>
			<button type="button" style="margin-left: 20px;"
				onclick="validar(4);">Cancelada</button>
			<button type="button" style="margin-left: 20px;"
				onclick="validar(0);">Danfe</button>

		</form>
	</div>

	<iframe id="nfeFrame"
		style="position: absolute; width: 0; height: 0; border: 0" src=""></iframe>

</body>
</html>
