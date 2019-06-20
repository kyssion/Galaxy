package com.kyssion.galaxy.script.translater.rule;

import com.kyssion.galaxy.script.translater.data.workKeyData.LexicalAnalysisData;
import com.kyssion.galaxy.script.translater.rule.base.EndRule;
import com.kyssion.galaxy.script.translater.rule.typeCheck.IdTypeRule;

/**
 * C = handleid
 */
public class CRule  extends EndRule {
    @Override
    public boolean isEnd() {
        return true;
    }

    @Override
    public boolean isMatch(LexicalAnalysisData data) {
        return data.getType().getCode() == 10 && IdTypeRule.isTrue(data.getValue());
    }
}
