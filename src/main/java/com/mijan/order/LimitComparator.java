package com.mijan.order;

import java.util.Comparator;

public class LimitComparator {

    public class BidLimitComparator implements Comparator<Limit> {

        @Override
        public int compare(Limit o1, Limit o2) {
            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 == null) {
                return 1;
            } else if (o2 == null) {
                return -1;

            }
            return Long.compare(o2.getPrice(), o1.getPrice());
        }
    }

    public class AskLimitComparator implements Comparator<Limit> {

        @Override
        public int compare(Limit o1, Limit o2) {
            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 == null) {
                return -1;
            } else if (o2 == null) {
                return 1;

            }
            return Long.compare(o1.getPrice(), o2.getPrice());
        }
    }

    public Comparator<Limit> askLimitComparator() {
        return new AskLimitComparator();
    }

    public Comparator<Limit> bidLimitComparator() {
        return new BidLimitComparator();
    }
}