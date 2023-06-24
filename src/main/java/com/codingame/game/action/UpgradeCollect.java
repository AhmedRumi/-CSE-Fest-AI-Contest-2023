package com.codingame.game.action;

import com.codingame.game.Config;
import com.codingame.game.Minion;

public class UpgradeCollect extends UpgradeSkill{
    public UpgradeCollect(Minion upgradedUser) {
        super(upgradedUser);
        this.price = Config.MINING_UPGRADE_PRICE;
        this.upgradeCountLimit = Config.UPGRADE_LIMITS[0];
    }

    @Override
    public UpgradeSkillType getUpgradeType() {
        return UpgradeSkillType.COLLECT;
    }

    @Override
    public boolean checkSkillLimit() {
        return !(Config.UPGRADE_LIMITS[0] == this.upgradedUser.getSkillLevel(0)); //
    }
}
