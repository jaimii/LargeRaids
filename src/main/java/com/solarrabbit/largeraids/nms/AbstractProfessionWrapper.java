package com.solarrabbit.largeraids.nms;

import java.util.function.Predicate;

public interface AbstractProfessionWrapper {
    Predicate<? super AbstractPoiTypeWrapper> getPredicate();
}
