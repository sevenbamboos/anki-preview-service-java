package samwang.anki.model;

import com.samwang.anki.impl.model.OutputLogCollector;
import com.samwang.anki.impl.model.TokenCard;

import java.util.List;
import java.util.stream.Collectors;

public class OutputLogCollectorDTO {
    private final OutputLogCollector collector;

    public OutputLogCollectorDTO(OutputLogCollector collector) {
        this.collector = collector;
    }

    public List<String> getIgnoredGroups() {
        return collector.getIgnoredGroups()
            .stream()
            .map(g -> g.name)
            .collect(Collectors.toList());
    }

    public List<String> getGroups() {
        return collector.getGroups()
            .stream()
            .map(g -> g.name)
            .collect(Collectors.toList());
    }

    public List<String> getErrorCards() {
        return collector.getErrorCards()
            .stream()
            .map(c -> (TokenCard)c)
            .map(tc -> tc.getGroupName().orElse("<No Group>") + ":" + tc.source())
            .collect(Collectors.toList());
    }

    public List<String> getClozeCards() {
        return collector.getCardsInCloze()
            .stream()
            .map(c -> (TokenCard)c)
            .map(tc -> tc.getRawClozeValue())
            .collect(Collectors.toList());
    }

    public List<String> getBasicCards() {
        return collector.getCardsInBasic()
            .stream()
            .map(c -> (TokenCard)c)
            .map(tc -> tc.getRawBasicValue())
            .collect(Collectors.toList());
    }
}
