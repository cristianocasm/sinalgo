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
package projects.leader.nodes.messages;

import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Message;

public class NetworkMessage extends Message {
	// Identificador da mensagem
	public Integer sequenceID;
	// Tempo de vida do Pacote
	public Integer ttl;
	// Nó de destino
	public Node destino;
	// Nó de origem
	public Node origem;
	// No que vai reencaminhar a mensagem
	public Node forwardingHop;
	// Número de saltos até o destino
	public Integer saltosAteDestino;
	// Tipo do Pacote. 0 para Estabelecimento de Rotas e 1 para pacotes de dados
	public Integer tipoMsg = 0;

	// Construtor da Classe
	public NetworkMessage(Integer seqID, Node origem, Node destino,
			Node forwardingHop, Integer tipo) {
		this.sequenceID = seqID;
		this.origem = origem;
		this.destino = destino;
		this.forwardingHop = forwardingHop;
		this.tipoMsg = tipo;
	}

	@Override
	public Message clone() {
		NetworkMessage msg = new NetworkMessage(this.sequenceID, this.origem,
				this.destino, this.forwardingHop, this.tipoMsg);
		msg.ttl = this.ttl;
		msg.saltosAteDestino = saltosAteDestino;
		return msg;
	}
}