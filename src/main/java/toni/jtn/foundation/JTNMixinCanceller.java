package toni.jtn.foundation;

import com.bawnorton.mixinsquared.api.MixinCanceller;

import java.util.List;

public class JTNMixinCanceller implements MixinCanceller {
    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        return mixinClassName.equals("terrablender.mixin.MixinMultiNoiseBiomeSource");
    }
}