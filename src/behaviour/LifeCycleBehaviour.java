package behaviour;

import agents.Village;
import exceptions.NotEnoughResources;
import utils.Economy;
import utils.Resource;
import utils.Logger;

import static utils.Printer.safePrintf;


public class LifeCycleBehaviour extends TimeTickerBehaviour {

    public LifeCycleBehaviour(Village village) {
        super(village);
    }

    public void consumeResources() {
        for (Resource r : this.village.getResources().values()) {
            try {
                r.consumeAmount(village.getResourceConsumption());
            } catch (NotEnoughResources e) {
                safePrintf("\t\t*** %s TERMINATED [%s] ***", village.getVillageName(), e.toString());
                //remove it from the info board
                Village.villagesInfo.remove(village.getVillageName());
                Economy.terminateVillage();
                this.village.doDelete();
            }
        }
    }

    public void produceResources() {
        for (Resource produced_resource : this.village.getProductionResources()) {
            village.getResources().get(produced_resource.getType()).produceAmount(produced_resource.getAmount());
        }
    }

    public void proposeTrades() {
        village.proposeTrades(village.generateDesiredTrades());
    }

    @Override
    protected void onTick() {
        consumeResources();
        produceResources();

        Logger.getInstance().logVillageStatus(
                this.village.tick_num,
                this.village.getVillageName(),
                this.village.getResources()
        );

        village.tick_num++;

        safePrintf("%s: %s-(%d) %s-(%d) %s-(%d) %s-(%d)", this.village.getVillageName(),
                Resource.ResourceType.STONE,
                this.village.getResources().get(Resource.ResourceType.STONE).getAmount(),
                Resource.ResourceType.WOOD,
                this.village.getResources().get(Resource.ResourceType.WOOD).getAmount(),
                Resource.ResourceType.FOOD,
                this.village.getResources().get(Resource.ResourceType.FOOD).getAmount(),
                Resource.ResourceType.CLAY,
                this.village.getResources().get(Resource.ResourceType.CLAY).getAmount()
        );

        proposeTrades();
    }
}
