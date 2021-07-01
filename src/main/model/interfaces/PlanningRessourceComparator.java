package main.model.interfaces;

import main.model.Intervention;
import main.model.Reparation;
import main.model.TrajetAller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class PlanningRessourceComparator implements Comparator<PlanningRessource> {


    @Override
    public int compare(PlanningRessource o1, PlanningRessource o2) {
        List<Date> dates = new ArrayList<>(2);
        dates.add(new Date());
        dates.add(new Date());

        List<PlanningRessource> params = List.of(o1, o2);
        for (int i=0; i < 2; i++) {
            if(params.get(i) instanceof Reparation) {
                dates.set(i, ((Reparation) params.get(i)).getDatePanne());
            } else if(params.get(i) instanceof Intervention) {
                dates.set(i, ((Intervention) params.get(i)).getDateIntervention());
            } else if(params.get(i) instanceof TrajetAller) {
                dates.set(i, ((TrajetAller) params.get(i)).getDateTrajet());
            } else {
                throw new UnsupportedOperationException();
            }
        }

        return dates.get(0).compareTo(dates.get(1));
    }
}
