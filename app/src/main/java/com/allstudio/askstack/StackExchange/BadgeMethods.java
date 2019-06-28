package com.allstudio.askstack.StackExchange;

import java.util.Date;
import java.util.concurrent.Future;

public final class BadgeMethods {
    private StacManClient client;

    BadgeMethods(StacManClient forClient) {
        client = forClient;
    }

    public Future<StacManResponse<Badge>> getAll(String site, String filter, Integer page, Integer pagesize, Date fromdate, Date todate, BadgeAllSort sort, BadgeRank minrank, BadgeRank maxrank, String minname, String maxname, BadgeType mintype, BadgeType maxtype, Order order, String inname)
    {
        if(sort == null){
            sort = BadgeAllSort.Default;
        }

        client.validateString(site, "site");
        client.validatePaging(page, pagesize);
        client.validateSortMinMax(sort, minrank, maxrank, minname, maxname, mintype, maxtype);

        ApiUrlBuilder ub = new ApiUrlBuilder("/badges", false);

        ub.addParameter("site", site);
        ub.addParameter("filter", filter);
        ub.addParameter("page", page);
        ub.addParameter("pagesize", pagesize);
        ub.addParameter("fromdate", fromdate);
        ub.addParameter("todate", todate);
        ub.addParameter("sort", sort);
        ub.addParameter("min", minrank);
        ub.addParameter("max", maxrank);
        ub.addParameter("min", minname);
        ub.addParameter("max", maxname);
        ub.addParameter("min", mintype);
        ub.addParameter("max", maxtype);
        ub.addParameter("order", order);
        ub.addParameter("inname", inname);

        return client.createApiTask(Types.Badge, ub, "/badges");
    }

    public Future<StacManResponse<Badge>> getByIds(String site, Iterable<Integer> ids, String filter, Integer page, Integer pagesize, Date fromdate, Date todate, BadgeAllSort sort, BadgeRank minrank, BadgeRank maxrank, String minname, String maxname, BadgeType mintype, BadgeType maxtype, Order order)
    {
        if(sort == null){
            sort = BadgeAllSort.Default;
        }

        client.validateString(site, "site");
        client.validateEnumerable(ids, "ids");
        client.validatePaging(page, pagesize);
        client.validateSortMinMax(sort, minrank, maxrank, minname, maxname, mintype, maxtype);

        ApiUrlBuilder ub = new ApiUrlBuilder(String.format("/badges/%1$S", StacManClient.join(";", ids)), false);

        ub.addParameter("site", site);
        ub.addParameter("filter", filter);
        ub.addParameter("page", page);
        ub.addParameter("pagesize", pagesize);
        ub.addParameter("fromdate", fromdate);
        ub.addParameter("todate", todate);
        ub.addParameter("sort", sort);
        ub.addParameter("min", minrank);
        ub.addParameter("max", maxrank);
        ub.addParameter("min", minname);
        ub.addParameter("max", maxname);
        ub.addParameter("min", mintype);
        ub.addParameter("max", maxtype);
        ub.addParameter("order", order);

        return client.createApiTask(Types.Badge, ub, "/badges/{ids}");
    }

    public Future<StacManResponse<Badge>> getNamed(String site, String filter, Integer page, Integer pagesize, Date fromdate, Date todate, BadgeSort sort, BadgeRank minrank, BadgeRank maxrank, String minname, String maxname, Order order, String inname)
    {
        if(sort == null){
            sort = BadgeSort.Default;
        }

        client.validateString(site, "site");
        client.validatePaging(page, pagesize);
        client.validateSortMinMax(sort, minrank, maxrank, minname, maxname);

        ApiUrlBuilder ub = new ApiUrlBuilder("/badges/name", false);

        ub.addParameter("site", site);
        ub.addParameter("filter", filter);
        ub.addParameter("page", page);
        ub.addParameter("pagesize", pagesize);
        ub.addParameter("fromdate", fromdate);
        ub.addParameter("todate", todate);
        ub.addParameter("sort", sort);
        ub.addParameter("min", minrank);
        ub.addParameter("max", maxrank);
        ub.addParameter("min", minname);
        ub.addParameter("max", maxname);
        ub.addParameter("order", order);
        ub.addParameter("inname", inname);

        return client.createApiTask(Types.Badge, ub, "/badges/name");
    }

    public Future<StacManResponse<Badge>> getRecent(String site, String filter, Integer page, Integer pagesize, Date fromdate, Date todate)
    {
        client.validateString(site, "site");
        client.validatePaging(page, pagesize);

        ApiUrlBuilder ub = new ApiUrlBuilder("/badges/recipients", false);

        ub.addParameter("site", site);
        ub.addParameter("filter", filter);
        ub.addParameter("page", page);
        ub.addParameter("pagesize", pagesize);
        ub.addParameter("fromdate", fromdate);
        ub.addParameter("todate", todate);

        return client.createApiTask(Types.Badge, ub, "/badges/recipients");
    }

    public Future<StacManResponse<Badge>> getRecentByIds(String site, Integer[] ids, String filter, Integer page, Integer pagesize, Date fromdate, Date todate) {
        return getRecentByIds(site, StacManClient.toIter(ids), filter, page, pagesize, fromdate,  todate);
    }

    public Future<StacManResponse<Badge>> getRecentByIds(String site, Iterable<Integer> ids, String filter, Integer page, Integer pagesize, Date fromdate, Date todate)
    {
        client.validateString(site, "site");
        client.validateEnumerable(ids, "ids");
        client.validatePaging(page, pagesize);

        ApiUrlBuilder ub = new ApiUrlBuilder(String.format("/badges/%1$S/recipients", StacManClient.join(";", ids)), false);

        ub.addParameter("site", site);
        ub.addParameter("filter", filter);
        ub.addParameter("page", page);
        ub.addParameter("pagesize", pagesize);
        ub.addParameter("fromdate", fromdate);
        ub.addParameter("todate", todate);

        return client.createApiTask(Types.Badge, ub, "/badges/{ids}/recipients");
    }

    public Future<StacManResponse<Badge>> getTagBased(String site, String filter, Integer page, Integer pagesize, Date fromdate, Date todate, BadgeSort sort, BadgeRank minrank, BadgeRank maxrank, String minname, String maxname, Order order, String inname)
    {
        if(sort == null){
            sort = BadgeSort.Default;
        }

        client.validateString(site, "site");
        client.validatePaging(page, pagesize);
        client.validateSortMinMax(sort, minrank, maxrank, minname, maxname);

        ApiUrlBuilder ub = new ApiUrlBuilder("/badges/tags", false);

        ub.addParameter("site", site);
        ub.addParameter("filter", filter);
        ub.addParameter("page", page);
        ub.addParameter("pagesize", pagesize);
        ub.addParameter("fromdate", fromdate);
        ub.addParameter("todate", todate);
        ub.addParameter("sort", sort);
        ub.addParameter("min", minrank);
        ub.addParameter("max", maxrank);
        ub.addParameter("min", minname);
        ub.addParameter("max", maxname);
        ub.addParameter("order", order);
        ub.addParameter("inname", inname);

        return client.createApiTask(Types.Badge, ub, "/badges/tags");
    }
}
