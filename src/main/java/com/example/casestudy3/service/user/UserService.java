package com.example.casestudy3.service.user;

import com.example.casestudy3.DAO.AdminDAO;
import com.example.casestudy3.DAO.UserDAO;
import com.example.casestudy3.controller.LogInServlet;
import com.example.casestudy3.controller.UserServlet;
import com.example.casestudy3.model.*;
import com.example.casestudy3.service.login.LoginService;
import com.example.casestudy3.service.singerService.SingerService;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserService {
    private UserDAO userDAO;
    private AdminDAO adminDAO;
    private LoginService loginService;

    public UserService() {
        userDAO = new UserDAO();
        adminDAO = new AdminDAO();
        loginService = new LoginService();
    }
    public boolean createPlayList(HttpServletRequest request){
        String name = request.getParameter("name");
        Date date = Date.valueOf(LocalDate.now());
        return userDAO.createPlayList(new Playlist(name,date,loginService.checkOnline()));
    }
    public User detailUser(HttpServletRequest request){
        return adminDAO.findByIdUser(loginService.checkOnline());
    }
    public boolean buySong(HttpServletRequest request)  {
        try {
            long songId = Long.parseLong(request.getParameter("id"));
            long playListId = Long.parseLong(request.getParameter("playList"));
            Song song = adminDAO.findByIdSong(songId);
            Singer singer = adminDAO.findByIdSinger(song.getSingerId());
            Long userId = loginService.checkOnline();
            double wallet = adminDAO.findByIdUser(userId).getWallet() - song.getPrice();
            double income = (singer.getIncome() + song.getPrice())*0.8;
            return userDAO.buySong(wallet,income,playListId,userId, singer.getId(), songId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public List<Song> searchSongByName(HttpServletRequest request)  {
        String text = request.getParameter("search");
        return userDAO.findSearchSongByName(text);
    }
    public List<Song> searchSongBySinger(HttpServletRequest request)  {
        String text = request.getParameter("search");
        Singer singer = adminDAO.findByNameSinger(text);
        return userDAO.findSearchSongBySinger(singer.getId());
    }
    public List<Song> searchSongByPlayList(HttpServletRequest request)  {
        String playList = request.getParameter("search");
        return userDAO.songByPlayList(playList);
    }
    public List<Song> findAllSong()  {
        List<Song> songs = userDAO.findAllSong();
        List<Song> songsUser = userDAO.songByUser(loginService.checkOnline());
        for (Song song : songs) {
            for (Song s : songsUser){
                if (s.getId() == song.getId()) {
                    song.setStatus(-1);
                    songsUser.remove(s);
                    break;
                }
            }
        }
        return songs;
    }
    public List<Song> listSongByUser()  {
        return userDAO.songByUser(loginService.checkOnline());
    }
    public double sumPriceBuySongUser()  {
        return userDAO.sumPriceBuySongUser(loginService.checkOnline());
    }
    public void deleteSongUser(HttpServletRequest request)  {
        long playListId =  Long.parseLong(request.getParameter("playListId"));
        long songId = Long.parseLong(request.getParameter("id"));
        userDAO.deleteSongByPlayList(playListId,songId);
    }
    public void addSongToPlayList(HttpServletRequest request) throws SQLException {
        boolean flag = true;
        List<PlaylistDetail> PlaylistDetails = userDAO.findAllPlayListDetail();
        long playListId =  Long.parseLong(request.getParameter("playListId"));
        long songId = Long.parseLong(request.getParameter("songId"));
        for (PlaylistDetail playlistDetail : PlaylistDetails){
            if(playlistDetail.getSongId() == songId && playlistDetail.getPlayListId() == playListId){
                flag = false;
               break;
            }
        }
        if (flag){
            userDAO.addPlayList(playListId, songId);
        }
    }
    public List<Playlist> findPlaylistUser(){
        return userDAO.playlistUser(loginService.checkOnline());
    }
    public void recharge(HttpServletRequest request) {
        try {
            double wallet = Double.parseDouble(request.getParameter("wallet"));
             userDAO.recharge(loginService.checkOnline(), wallet);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    public Map<Long, Singer> mapListSinger(){
        return userDAO.mapListSinger();
    }
//    public List<Singer> listSinger(){
//        List<Singer> singers = new ArrayList<>();
//        for (Song song: findAllSong()){
//            singers.add(findSingerById(song.getSingerId()));
//        }
//        return singers;
//    }
//    public Singer findSingerById(long id ){
//        for (Singer s: adminDAO.findAllSinger()){
//            if  (s.getId() == id){
//                return s;
//            }
//        }
//        return null;
//    }
}
