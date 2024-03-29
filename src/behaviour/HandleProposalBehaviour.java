package behaviour;

import agents.Village;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import utils.Trade;

import java.io.IOException;

/**
 * Waits for other initiators to start trades
 */
public class HandleProposalBehaviour extends CyclicBehaviour {

    private static final MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);

    @Override
    public void action() {
        ACLMessage msg = this.myAgent.receive(mt);

        if (msg != null) {
            try {
                Trade trade = (Trade) msg.getContentObject();
                Village village = (Village) this.getAgent();
                ACLMessage reply = msg.createReply();

                if (village.canAcceptTrade(trade) && village.wantToAcceptTrade(trade)) {
                    Trade counter_propose = village.decideCounterPropose(trade);

                    /*  start accounting for the promised quantity
                     *  since we are the receiver, that resource is the request
                     */
                    village.accountForNewTrade(counter_propose.getRequest());

                    reply.setContentObject(counter_propose);
                    reply.setPerformative(ACLMessage.PROPOSE);
                } else {
                    reply.setPerformative(ACLMessage.REFUSE);
                }
                this.myAgent.send(reply);
            } catch (UnreadableException | IOException e) {
                e.printStackTrace();
            }
        }
        else {
            block();
        }
    }
}
