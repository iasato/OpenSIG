package br.com.opensig.core.server;

import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Classe que gerencia os robos de auto execucao do sistema.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public class RoboServiceImpl extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		Properties conf = new Properties();
		Properties tarefas = new Properties();

		// pega os parametros do quarts e tarefas se tiver
		Enumeration<String> param = getInitParameterNames();
		for (; param.hasMoreElements();) {
			String nome = param.nextElement();
			String valor = getInitParameter(nome);
			if (nome.startsWith("org.quartz")) {
				conf.put(nome, valor);
			} else {
				tarefas.put(nome, valor);
			}
		}

		// verifica se tem tarefas para o robo
		if (!tarefas.isEmpty()) {
			try {
				// criando o agendamento
				StdSchedulerFactory fac = new StdSchedulerFactory();
				fac.initialize(conf);
				Scheduler scheduler = fac.getScheduler();

				// percorre para achar as tarefas do robo
				for (Entry<Object, Object> task : tarefas.entrySet()) {
					// pega o nome da classe que executa a tarefa
					String nome = task.getKey().toString();
					// pega o tempo em segundos de espera para repetir a tarefa
					int tempo = Integer.valueOf(task.getValue().toString());
					try {
						Class<Job> classe = (Class<Job>) Class.forName(nome);
						// adicionando o job
						JobDetail job = JobBuilder.newJob(classe).withIdentity(nome, "robo").build();
						// setando a tarefa para executar a cada X segundos
						Trigger tri = TriggerBuilder.newTrigger().withIdentity(classe.getSimpleName()).startNow().withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(tempo)).build();
						// adicionando a tarefa ao job
						scheduler.scheduleJob(job, tri);
					} catch (Exception e) {
						UtilServer.LOG.error("A classe [" + nome + "] nao foi encontrada.", e);
					}
				}
				scheduler.start();
			} catch (SchedulerException se) {
				UtilServer.LOG.error("Erro ao iniciar o robo.", se);
			}
		}
	}
}
