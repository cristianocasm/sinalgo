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

import projects.leader.nodes.messages.NetworkMessage;
import projects.leader.nodes.timers.NetworkMessageTimer;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;

/**
 * The Node of the sample project.
 */
public class SimpleNode extends Node {

	// Armazenar o nó que sera usado para alcançar a Estacao-Base
	private Node proximoNoAteEstacaoBase;
	// Armazena o número de sequencia da última mensagem recebida
	private Integer sequenceNumber = 0;

	@Override
	public void handleMessages(Inbox inbox) {
		while (inbox.hasNext()) {
			Message message = inbox.next();
			if (message instanceof NetworkMessage) {
				Boolean encaminhar = Boolean.TRUE;
				NetworkMessage wsnMessage = (NetworkMessage) message;
				if (wsnMessage.forwardingHop.equals(this)) { // A mensagem
																// voltou. O no
																// deve
																// descarta-la
					encaminhar = Boolean.FALSE;
				} else if (wsnMessage.tipoMsg == 0) { // A mensagem é um flood.
														// Devemos atualizar a
														// rota
					if (proximoNoAteEstacaoBase == null) {
						proximoNoAteEstacaoBase = inbox.getSender();
						sequenceNumber = wsnMessage.sequenceID;
					} else if (sequenceNumber < wsnMessage.sequenceID) {
						// Recurso simples para evitar loop.
						// Exemplo: Noh A transmite em brodcast. Noh B recebe a
						// msg e retransmite em broadcast.
						// Consequentemente, noh A irá receber a msg. Sem esse
						// condicional, noh A iria retransmitir novamente,
						// gerando um loop
						sequenceNumber = wsnMessage.sequenceID;
					} else {
						encaminhar = Boolean.FALSE;
					}
				}
				if (encaminhar) {
					// Devemos alterar o campo forwardingHop(da
					// mensagem) para armazenar o
					// noh que vai encaminhar a mensagem.
					wsnMessage.forwardingHop = this;
					broadcast(wsnMessage);
				}
			}
		}
	}
	
	@NodePopupMethod(menuText="Construir Arvore de Roteamento")
	public void construirRoteamento(){
	this.proximoNoAteEstacaoBase = this;
	NetworkMessage wsnMessage = new NetworkMessage(1, this, null, this, 0);
	NetworkMessageTimer timer = new NetworkMessageTimer(wsnMessage);
	timer.startRelative(1, this);
	}

	@Override
	public void preStep() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void neighborhoodChange() {
		// TODO Auto-generated method stub

	}

	@Override
	public void postStep() {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkRequirements() throws WrongConfigurationException {
		// TODO Auto-generated method stub

	}
}
