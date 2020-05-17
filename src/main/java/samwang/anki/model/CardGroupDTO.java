package samwang.anki.model;

import com.samwang.anki.impl.model.CardGroup;

import java.util.List;
import java.util.stream.Collectors;

public class CardGroupDTO {
    private final CardGroup source;

    public CardGroupDTO(CardGroup source) {
        this.source = source;
    }

    public boolean isNew() {
        return source.isNew();
    }

    public boolean isChanged() {
        return source.isChanged();
    }

    public String getName() {
        return source.name;
    }

    public List<CardDTO> getCards() {
        return source.getCards().stream()
            .map(x -> new CardDTO(x))
            .collect(Collectors.toList());
    }

    public List<CardDTO> getPreviewCards() {
        return source.getPreviewCards().stream()
            .map(x -> new CardDTO(x))
            .collect(Collectors.toList());
    }
}
