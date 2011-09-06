<%@page import="br.com.opensig.core.server.UtilServer"%>
<%@page import="nl.captcha.Captcha"%>
<%@page import="br.com.opensig.core.client.controlador.filtro.IFiltro"%>
<%@page import="br.com.opensig.core.client.controlador.filtro.EJuncao"%>
<%@page import="br.com.opensig.core.client.controlador.filtro.GrupoFiltro"%>
<%@page import="br.com.opensig.core.client.controlador.filtro.ECompara"%>
<%@page	import="br.com.opensig.core.client.controlador.filtro.FiltroTexto"%>
<%@page import="br.com.opensig.permissao.shared.modelo.SisUsuario"%>
<%@page import="br.com.opensig.core.server.CoreServiceImpl"%>
<%@page import="br.com.opensig.core.client.servico.CoreService"%>
<%@page import="java.util.Date"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
	String url = request.getContextPath() + "/CoreService?data=" + new Date().getTime();
	String email = request.getParameter("email");
	String id = request.getParameter("id");
	String nova = request.getParameter("nova");
	String captcha = request.getParameter("captcha");
	String msg = "";
	String redir = "document.location = '/';";

	if (email == null || id == null || email.isEmpty() || id.isEmpty()) {
		msg = "alert('Email ou Id inválidos');" + redir;
	} else {
		SisUsuario usuario = new SisUsuario();
		FiltroTexto ft1 = new FiltroTexto("sisUsuarioEmail", ECompara.IGUAL, email);
		FiltroTexto ft2 = new FiltroTexto("sisUsuarioSenha", ECompara.IGUAL, id);
		GrupoFiltro gf = new GrupoFiltro(EJuncao.E, new IFiltro[]{ft1, ft2});
		CoreService core = new CoreServiceImpl();
		usuario = (SisUsuario) core.selecionar(usuario, gf, false);

		if (usuario == null) {
			msg = "alert('Usuário não encontrado!');" + redir;
		} else if (nova != null && captcha != null && !nova.isEmpty() && !captcha.isEmpty()) {
			HttpSession sessao = request.getSession();
			Captcha cap = (Captcha) sessao.getAttribute(Captcha.NAME);
			if (!cap.isCorrect(captcha)) {
				msg = "alert('Código da imagem inválido!');";
			} else {
				nova = UtilServer.SHA1(nova);
				usuario.setSisUsuarioSenha(nova);
				core.salvar(usuario);
				msg = "alert('Senha alterada com sucesso!');" + redir;
			}
		}
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<style>
html,body {
	background: #C4C4C4;
	margin: 0;
	padding: 0;
	margin-left: 0px; margin-top : 0px;
	font-family: "Lucida Grande", "Lucida Sans Unicode", Verdana, Arial,
		Helvetica, sans-serif;
	font-size: 12px;
	margin-top: 0px;
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
	height: 280px;
	padding: 14px;
	position: absolute;
	left: 50%;
	top: 50%;
	margin-left: -267px;
	margin-top: -147px;
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
	position: absolute;
	left: 50%;
	margin-left: -62px;
}
</style>

<script type="text/javascript">
	<%=msg%>

	function validar(){
		var strNova = document.senha.nova.value;
		var strConfirma = document.senha.confirma.value;
		var strCaptcha = document.senha.captcha.value;
		
		if(strNova == "" || strNova.length < 6)
		{
			alert( "Preencha a nova senha com 6 caracteres no mínimo!" );
			document.senha.nova.focus();
			return false;
		}
		else if(strConfirma != strNova)
		{
			alert( "As duas senhas não são iguais!" );
			document.senha.confirma.focus();
			return false;
		}
		else if(strCaptcha == "")
		{
			alert( "Preencha o código da imagem!" );
			document.senha.captcha.focus();
			return false;
		}
		else {
			return true;
		}
	}
	
</script>

<title>OpenSIG - Sistemas Integrados Gerenciáveis Open Source</title>
</head>
<body onload="javascript:document.senha.nova.focus();">
	<div id="stylized" class="myform">

		<form id="senha" name="senha" method="post"
			action="novasenha.jsp?email=<%=email%>&id=<%=id%>"
			onsubmit="return validar();">
			<a href="http://opensig.com.br" title="OpenSIG"><img
				src="img/logo.png" width="32px" height="32px" /> </a>
			<h1>Alterar Senha</h1>
			<p>Este formulário &eacute; usado para alterar a senha no
				sistema.</p>
			<label>Nova Senha <span class="small">Digite a nova
					senha</span> </label> <input type="password" name="nova" id="nova" maxlength="40" />
			<label>Confirmar Senha <span class="small">Confirme a
					senha</span> </label> <input type="password" name="confirma" id="confirma"
				maxlength="40" /> <label>Segurança <span class="small">Veja
					a imagem</span> </label> <img src="<%=url%>" /> <label>Código <span
				class="small">Digite o código</span> </label> <input type="text"
				name="captcha" id="captcha" maxlength="5" class="small" />

			<div class="spacer"></div>
			<button type="submit">Salvar</button>

		</form>
	</div>
</body>
</html>
