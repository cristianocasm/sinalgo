/*
 Copyright (c) 2007, Distributed Computing Group (DCG)
                    ETH Zurich
                    Switzerland
                    dcg.ethz.ch

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 - Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

 - Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the
   distribution.

 - Neither the name 'Sinalgo' nor the names of its contributors may be
   used to endorse or promote products derived from this software
   without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package projects.leader.nodes.nodeImplementations;

import java.awt.Color;
import java.util.ArrayList;

import projects.leader.nodes.messages.NetworkMessage;
import projects.leader.nodes.timers.NetworkMessageTimer;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;
import sinalgo.runtime.Runtime;

/**
 * The Node of the sample project.
 */
public class SimpleNode extends Node {

	private final int ELECTION = 0;
	private final int STOP = 1;
	private final int PING = 2;
	private final int PONG = 3;

	// private boolean isLeader = false;
	private SimpleNode networkLeader = null;

	/**
	 * Seta o presente nó como o líder da rede
	 * */
	public void setAsNetworkLeader() {
		// this.isLeader = true;
		this.networkLeader = this;
		this.setColor(Color.RED);
		this.proclaimLeadership();
	}

	/**
	 * Proclama o presente nó como leader da rede
	 * */
	private void proclaimLeadership() {
		// chama método de envio de mensagem passando this como mensagem
	}

	/**
	 * Armazena a informação de quem é o líder da rede
	 * */
	private void setLeader(SimpleNode leader) {
		this.networkLeader = leader;
	}

	/**
	 * Inicia eleição para definir o líder da rede
	 * */
	private void startLeaderElection() {
		ArrayList<SimpleNode> nbhs = this.getHigherIDNeighborhoods();
		this.fireLeaderElectionMsg(nbhs);

		// Envia mensagem para nós com ID superiores
		// Se não houver resposta dentro de tempo hábil, chama
		// proclaimLeadership
		// Caso contrário, desiste
	}
	
//	private void sendMsg(SimpleNode sn) {
//	NetworkMessage wsnMessage = new NetworkMessage(1, this, null, this, 0);
//	NetworkMessageTimer timer = new NetworkMessageTimer(wsnMessage);
//	timer.startRelative(1, this);
//}

	private ArrayList<SimpleNode> getHigherIDNeighborhoods() {
		ArrayList<SimpleNode> neighborhoods = new ArrayList<SimpleNode>();

		for (Node n : Runtime.nodes) {
			if (n.ID > this.ID) {
				neighborhoods.add((SimpleNode) n);
			}
		}

		return neighborhoods;
	}

	private void fireLeaderElectionMsg(ArrayList<SimpleNode> neighborhoods) {
		for (SimpleNode sn : neighborhoods) {
			//this.send(new NetworkMessage(ELECTION), sn);
			NetworkMessageTimer timer = new NetworkMessageTimer(new NetworkMessage(ELECTION));
			timer.startRelative(1, this);
		}
	}

	/**
	 * TODO verificar como funciona o envio de mensagens TODO adicionar
	 * parâmetro "mensagem" com o conteúdo da mensagem
	 * */
//	private void sendMsg(SimpleNode sn) {
//		NetworkMessage wsnMessage = new NetworkMessage(1, this, null, this, 0);
//		NetworkMessageTimer timer = new NetworkMessageTimer(wsnMessage);
//		timer.startRelative(1, this);
//	}

	/**
	 * TODO verificar como checar retorno
	 * */
	@NodePopupMethod(menuText = "Ping Leader")
	public void pingLeader() {
		if(this.networkLeader == null)
			startLeaderElection();
		else
			this.send(new NetworkMessage(PING), this.networkLeader);

		// Envia mensagem para líder utilizando this.NetworkLeader
		// Caso retorno seja dado, envia para output "Leader (#ID): Pong "
		// Caso contrário chama método "startLeaderElection"
	}

	// Armazenar o nó que sera usado para alcançar a Estacao-Base
	private Node proximoNoAteEstacaoBase;
	// Armazena o número de sequencia da última mensagem recebida
	private Integer sequenceNumber = 0;

	/**
	 * This method is invoked after all the Messages are received. Overwrite it
	 * to specify what to do with incoming messages.
	 * 
	 * @param inbox
	 *            a instance of a iterator-like class Inbox. It is used to
	 *            traverse the incoming packets and to get information about
	 *            them.
	 * @see Node#step() for the order of calling the methods.
	 */
	@Override
	public void handleMessages(Inbox inbox) {
		while (inbox.hasNext()) {
			Message message = inbox.next();			
			
			if (message instanceof NetworkMessage) {
				Node sender = inbox.getSender();
				
				switch(((NetworkMessage) message).tipoMsg){
					case 0: // ELECTION
						if(this.ID > sender.ID)
							this.send(new NetworkMessage(STOP), sender);
						break;
					case 1: // STOP TODO
						break;
					case 2: // PING
						if(this.ID == this.networkLeader.ID)
							this.send(new NetworkMessage(PONG), sender);
						break;
					case 3: // PONG TODO
						break;
				}
				
//				Boolean encaminhar = Boolean.TRUE;
//				NetworkMessage wsnMessage = (NetworkMessage) message;
//				if (wsnMessage.forwardingHop.equals(this)) { // A mensagem
//																// voltou. O no
//																// deve
//																// descarta-la
//					encaminhar = Boolean.FALSE;
//				} else if (wsnMessage.tipoMsg == 0) { // A mensagem é um flood.
//														// Devemos atualizar a
//														// rota
//					if (proximoNoAteEstacaoBase == null) {
//						proximoNoAteEstacaoBase = inbox.getSender();
//						sequenceNumber = wsnMessage.sequenceID;
//					} else if (sequenceNumber < wsnMessage.sequenceID) {
//						// Recurso simples para evitar loop.
//						// Exemplo: Noh A transmite em brodcast. Noh B recebe a
//						// msg e retransmite em broadcast.
//						// Consequentemente, noh A irá receber a msg. Sem esse
//						// condicional, noh A iria retransmitir novamente,
//						// gerando um loop
//						sequenceNumber = wsnMessage.sequenceID;
//					} else {
//						encaminhar = Boolean.FALSE;
//					}
//				}
//				if (encaminhar) {
//					// Devemos alterar o campo forwardingHop(da
//					// mensagem) para armazenar o
//					// noh que vai encaminhar a mensagem.
//					wsnMessage.forwardingHop = this;
//					broadcast(wsnMessage);
//				}
			}
		}
	}

	// @NodePopupMethod(menuText = "Construir Arvore de Roteamento")
	// public void construirRoteamento() {
	// this.proximoNoAteEstacaoBase = this;
	// NetworkMessage wsnMessage = new NetworkMessage(1, this, null, this, 0);
	// NetworkMessageTimer timer = new NetworkMessageTimer(wsnMessage);
	// timer.startRelative(1, this);
	// }

	// @NodePopupMethod(menuText="Set Leader")
	// public void setLeader(){
	// this.setColor(Color.red);
	// }

	/**
	 * This method is invoked at the beginning of each step. Add actions to this
	 * method that this node should perform in every step.
	 * 
	 * @see Node#step() for the calling sequence of the node methods.
	 */
	@Override
	public void preStep() {
		// TODO Auto-generated method stub

	}

	/**
	 * This method is called exactly once upon creation of this node and allows
	 * the subclasses to perform some node-specific initialization.
	 * <p>
	 * When a set of nodes is generated, this method may be called before all
	 * nodes are added to the framework. Therefore, this method should not
	 * depend on other nodes of the framework.
	 */
	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	/**
	 * At the beginning of each round, the framework moves all nodes according
	 * to their mobility model. Then, it iterates over all nodes to update the
	 * connections, according to the nodes connectivity model.
	 * <p>
	 * This method is called in the step of this node if the set of outgoing
	 * connections had changed in this round. I.e. a new edge was added or an
	 * edge was removed.
	 * <p>
	 * As a result, this method is called nearly always in the very first round,
	 * when the network graph is determined for the first time.
	 */
	@Override
	public void neighborhoodChange() {
		// TODO Auto-generated method stub

	}

	/**
	 * The node calls this method at the end of its step.
	 */
	@Override
	public void postStep() {
		// TODO Auto-generated method stub

	}

	/**
	 * This method checks if the configuration meets the specification of the
	 * node. This function is called exactly once just after the initialisazion
	 * of a node but before the first usage.
	 * 
	 * @throws WrongConfigurationException
	 *             if the requirements are not met.
	 */
	@Override
	public void checkRequirements() throws WrongConfigurationException {
		// TODO Auto-generated method stub

	}
}
