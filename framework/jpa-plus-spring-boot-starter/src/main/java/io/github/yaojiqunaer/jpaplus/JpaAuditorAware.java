package io.github.yaojiqunaer.jpaplus;

import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class JpaAuditorAware implements AuditorAware<Long> {

    @NotNull
    @Override
    public Optional<Long> getCurrentAuditor() {
        return Optional.empty();
    }

}
