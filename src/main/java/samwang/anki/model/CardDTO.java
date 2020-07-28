package samwang.anki.model;

import com.samwang.anki.impl.model.Card;
import com.samwang.anki.impl.model.TokenCard;

public class CardDTO {

    private final TokenCard source;

    public CardDTO(TokenCard source) {
        this.source = source;
    }

    public CardDTO(Card source) {
        // FIXME only support token-based card
        this.source = (TokenCard) source;
    }

    public boolean isError() {
        return source.hasError();
    }

    public String getTags() {
        return source.getTags();
    }

    public String getGroupName() {
        return source.getGroupName().orElse("");
    }

    public boolean isForCloze() {
        return source.isForCloze();
    }

    public boolean isForBasic() {
        return source.isForBasic();
    }

    public String getCloze() {
        try {
            return source.getRawClozeValue();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String getBasic() {
        try {
            return source.getRawBasicValue();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String getSource() {
        return source.source;
    }
}
