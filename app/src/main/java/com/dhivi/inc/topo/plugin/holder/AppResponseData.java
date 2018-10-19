package com.dhivi.inc.topo.plugin.holder;

import com.dhivi.inc.topo.fragments.InstantTopos;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Manoj on 10/28/2017.
 */

public class AppResponseData {

    @SerializedName("result")
    public String result;
    @SerializedName("msg")
    public String msg;
    public String requiredActionb;
    public Authentication signup;
    public Authentication singin;
    ArrayList<TopoRequests>requests;
    ArrayList<InstantTopos>instantTopos;
    ArrayList<InstantTopos>topoInstantView;
    ArrayList<Topos>topos;
    ArrayList<Topos>savedToposList;
    TopoInstantCreate topoInstantCreate;
    ArrayList<FetchContacts>fetchContacts;
    TopoCreate topoCreate;
    ArrayList<Topos>topoView;
    ArrayList<ArrivingTopos>arrivings;
    ArrayList<LiveUsers>liveUsers;
    ArrayList<LiveTopo>liveTopo;
    ProfileView profileView;
    ArrayList<AccessList>accessList;
    StartNavigation startNavigation;
    String top_id;
    String topo_namee;
    ArrayList<Topos>searchDefault;
    ArrayList<Topos>searchTopo;
    ArrayList<CabsData>cabs;
    ArrayList<NearByData>nearby;
    FbOauth fbOauth;
    String message;
    String type;
    ArrayList<FeedbackList>feedbackList;
    ArrayList<CounriesList>counriesList;
    //public Data data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRequiredActionb() {
        return requiredActionb;
    }

    public void setRequiredActionb(String requiredActionb) {
        this.requiredActionb = requiredActionb;
    }

    public Authentication getSignup() {
        return signup;
    }

    public void setSignup(Authentication signup) {
        this.signup = signup;
    }

    public Authentication getSingin() {
        return singin;
    }

    public void setSingin(Authentication singin) {
        this.singin = singin;
    }

    public ArrayList<TopoRequests> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<TopoRequests> requests) {
        this.requests = requests;
    }

    public ArrayList<InstantTopos> getInstantTopos() {
        return instantTopos;
    }

    public void setInstantTopos(ArrayList<InstantTopos> instantTopos) {
        this.instantTopos = instantTopos;
    }

    public ArrayList<Topos> getTopos() {
        return topos;
    }

    public void setTopos(ArrayList<Topos> topos) {
        this.topos = topos;
    }

    public ArrayList<Topos> getSavedToposList() {
        return savedToposList;
    }

    public void setSavedToposList(ArrayList<Topos> savedToposList) {
        this.savedToposList = savedToposList;
    }

    public TopoInstantCreate getTopoInstantCreate() {
        return topoInstantCreate;
    }

    public void setTopoInstantCreate(TopoInstantCreate topoInstantCreate) {
        this.topoInstantCreate = topoInstantCreate;
    }

    public ArrayList<FetchContacts> getFetchContacts() {
        return fetchContacts;
    }

    public void setFetchContacts(ArrayList<FetchContacts> fetchContacts) {
        this.fetchContacts = fetchContacts;
    }

    public TopoCreate getTopoCreate() {
        return topoCreate;
    }

    public void setTopoCreate(TopoCreate topoCreate) {
        this.topoCreate = topoCreate;
    }

    public ArrayList<Topos> getTopoView() {
        return topoView;
    }

    public void setTopoView(ArrayList<Topos> topoView) {
        this.topoView = topoView;
    }

    public ArrayList<ArrivingTopos> getArrivings() {
        return arrivings;
    }

    public void setArrivings(ArrayList<ArrivingTopos> arrivings) {
        this.arrivings = arrivings;
    }

    public ArrayList<LiveUsers> getLiveUsers() {
        return liveUsers;
    }

    public void setLiveUsers(ArrayList<LiveUsers> liveUsers) {
        this.liveUsers = liveUsers;
    }

    public ArrayList<LiveTopo> getLiveTopo() {
        return liveTopo;
    }

    public void setLiveTopo(ArrayList<LiveTopo> liveTopo) {
        this.liveTopo = liveTopo;
    }

    public ProfileView getProfileView() {
        return profileView;
    }

    public void setProfileView(ProfileView profileView) {
        this.profileView = profileView;
    }

    public ArrayList<InstantTopos> getTopoInstatnView() {
        return topoInstantView;
    }

    public void setTopoInstatnView(ArrayList<InstantTopos> topoInstatnView) {
        this.topoInstantView = topoInstatnView;
    }

    public ArrayList<InstantTopos> getTopoInstantView() {
        return topoInstantView;
    }

    public void setTopoInstantView(ArrayList<InstantTopos> topoInstantView) {
        this.topoInstantView = topoInstantView;
    }

    public ArrayList<AccessList> getAccessList() {
        return accessList;
    }

    public void setAccessList(ArrayList<AccessList> accessList) {
        this.accessList = accessList;
    }

    public StartNavigation getStartNavigation() {
        return startNavigation;
    }

    public void setStartNavigation(StartNavigation startNavigation) {
        this.startNavigation = startNavigation;
    }

    public String getTopo_id() {
        return top_id;
    }

    public void setTopo_id(String topo_id) {
        this.top_id = top_id;
    }

    public String getTopo_namee() {
        return topo_namee;
    }

    public void setTopo_namee(String topo_namee) {
        this.topo_namee = topo_namee;
    }

    public String getTop_id() {
        return top_id;
    }

    public void setTop_id(String top_id) {
        this.top_id = top_id;
    }

    public ArrayList<Topos> getSearchDefalut() {
        return searchDefault;
    }

    public void setSearchDefalut(ArrayList<Topos> searchDefalut) {
        this.searchDefault = searchDefalut;
    }

    public ArrayList<Topos> getSearchTopo() {
        return searchTopo;
    }

    public void setSearchTopo(ArrayList<Topos> searchTopo) {
        this.searchTopo = searchTopo;
    }

    public ArrayList<Topos> getSearchDefault() {
        return searchDefault;
    }

    public void setSearchDefault(ArrayList<Topos> searchDefault) {
        this.searchDefault = searchDefault;
    }

    public ArrayList<CabsData> getCabs() {
        return cabs;
    }

    public void setCabs(ArrayList<CabsData> cabs) {
        this.cabs = cabs;
    }

    public ArrayList<NearByData> getNearby() {
        return nearby;
    }

    public void setNearby(ArrayList<NearByData> nearby) {
        this.nearby = nearby;
    }

    public FbOauth getFbOauth() {
        return fbOauth;
    }

    public void setFbOauth(FbOauth fbOauth) {
        this.fbOauth = fbOauth;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<FeedbackList> getFeedbackList() {
        return feedbackList;
    }

    public void setFeedbackList(ArrayList<FeedbackList> feedbackList) {
        this.feedbackList = feedbackList;
    }

    public ArrayList<CounriesList> getCounriesList() {
        return counriesList;
    }

    public void setCounriesList(ArrayList<CounriesList> counriesList) {
        this.counriesList = counriesList;
    }
/*public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }*/

}
