package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private  HashMap<String, User> userData = new HashMap<>();
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser(String name, String mobile) throws Exception {
        if(userMobile.contains(mobile)){
            throw new Exception("User already exists");
        }

        User user = new User(name, mobile);
        userData.put(name, user);
        userMobile.add(mobile);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {
        int size = users.size();
        Group group;
        if(size > 2){
            this.customGroupCount += 1;
            String grpName = "Group "+customGroupCount;
            group = new Group(grpName, size);
        }
        else{
            String grpName = users.get(1).getName();
            group = new Group(grpName, size);
        }
        User admin = users.get(0);
        adminMap.put(group, admin);
        List<Message> messageList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        groupMessageMap.put(group, messageList);
        groupUserMap.put(group, users);
        return group;
    }

    public int createMessage(String content) {
        this.messageId += 1;
        Message message = new Message(messageId, content);
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        List<User> list = groupUserMap.get(group);
        boolean flag = false;
        for(User user: list){
            if(user.equals(sender)){
                flag = true;
                senderMap.put(message, sender);
                List<Message> messageList = groupMessageMap.get(group);
                messageList.add(message);
                groupMessageMap.put(group, messageList);
                return messageList.size();
            }
        }
        if(flag == false){
            throw new Exception("You are not allowed to send message");
        }
        return groupMessageMap.get(group).size();
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        if(!adminMap.get(group).equals(approver)){
            throw new Exception("Approver does not have rights");
        }
        List<User> users = groupUserMap.get(group);
        for(User u : users){
            if(u.equals(user)) {
                adminMap.put(group, user);
                return "SUCCESS";
            }
        }
        throw new Exception("User is not a participant");
    }
}
