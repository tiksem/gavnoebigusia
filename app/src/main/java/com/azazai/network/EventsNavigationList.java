package com.azazai.network;

import com.jsonandroid.JsonAsyncNavigationList;
import com.azazai.data.Event;
import com.utils.framework.KeyProvider;
import com.utils.framework.network.RequestExecutor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by CM on 6/21/2015.
 */
class EventsNavigationList extends JsonAsyncNavigationList<Event> {
    private boolean actualEventsLoaded = false;
    private int actualPagesLoadedCount = -1;

    public EventsNavigationList(String url, String jsonKey,
                                Map<String, Object> args,
                                RequestExecutor requestExecutor,
                                RequestManager requestManager) {
        super(Event.class, url, jsonKey, args, requestExecutor, requestManager);
    }

    @Override
    protected boolean isLastPage(List<Event> elements, int limit) {
        boolean isLastPage = super.isLastPage(elements, limit);
        if(!isLastPage) {
            return false;
        }

        if (actualEventsLoaded) {
            return true;
        } else {
            actualEventsLoaded = true;
            getArgs().put("timeOut", true);
            return false;
        }
    }

    @Override
    protected int getOffset() {
        if (actualEventsLoaded) {
            if (actualPagesLoadedCount < 0) {
                actualPagesLoadedCount = getLoadedPagesCount();
            }

            return super.getOffset() - actualPagesLoadedCount * getLimit();
        } else {
            return super.getOffset();
        }
    }

    @Override
    protected KeyProvider<Object, Event> getKeyProvider() {
        return new KeyProvider<Object, Event>() {
            @Override
            public Object getKey(Event event) {
                return event.id;
            }
        };
    }
}