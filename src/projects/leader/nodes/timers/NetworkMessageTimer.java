package projects.leader.nodes.timers;

import java.util.ArrayList;

import projects.leader.nodes.messages.NetworkMessage;
import projects.leader.nodes.nodeImplementations.SimpleNode;
import sinalgo.nodes.timers.Timer;

public class NetworkMessageTimer extends Timer {
	private NetworkMessage message = null;

	public NetworkMessageTimer(NetworkMessage message) {
		this.message = message;
	}

	@Override
	public void fire() {
		// ((SimpleNode) node).broadcast(message);
		ArrayList<SimpleNode> neighborhoods = ((SimpleNode) node).getHigherIDNeighborhoods();
		for(SimpleNode sn : neighborhoods){
			((SimpleNode) node).send(this.message, sn);
		}
	}
}
