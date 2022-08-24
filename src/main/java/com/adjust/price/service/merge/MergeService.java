package com.adjust.price.service.merge;

import com.adjust.price.model.Price;

import java.util.*;

public interface MergeService {

    Collection<Price> merge(List<Price> oldPrices, List<Price> newPrices);

}
