package com.summertaker.member48;

import com.summertaker.member48.common.BaseParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;

public class Akb48Parser extends BaseParser {

    public void parseMemberList(String response, ArrayList<MemberData> memberList) {
        /*
        <ul class="infoList">
        <li>
        <a href="./detail/index.php?artist_code=83100536&g_code=all" data-ajax="false">
          <div class="textCenterBox">
            <div class="photo"><img class="lazy borderPink" data-original="http://image.excite.co.jp/jp/akb48/image/smartphone/20160509/profile/thumb/83100536.jpg" alt="入山杏奈"></div>
            <div class="text02">
              <p class="textbBld pnk fL">入山杏奈</p>
              <p class="lineRight colorPink02 r fR f12">Anna Iriyama</p>
            </div>
          </div>
        </a>
        </li>
        */
        if (response == null || response.isEmpty()) {
            return;
        }

        Document doc = Jsoup.parse(response);
        Element root = doc.select(".infoList").first();

        if (root != null) {
            for (Element row : root.select("li")) {
                String name;
                String nameEn;
                String noSpaceName;
                String thumbnailUrl;
                String profileUrl;

                Element el;

                Element a = row.select("a").first();
                profileUrl = a.attr("href");
                profileUrl = profileUrl.replace("./", "/");
                profileUrl = "http://sp.akb48.co.jp/profile/member" + profileUrl;

                Element img = a.select("img").first();
                if (img == null) {
                    continue;
                }
                thumbnailUrl = img.attr("data-original");
                //Log.e(mTag, thumbnailUrl);

                el = a.select(".textbBld").first();
                if (el == null) {
                    continue;
                }
                name = el.text().trim();

                el = a.select(".lineRight").first();
                if (el == null) {
                    continue;
                }
                nameEn = el.text().trim();

                //Log.e(mTag, name + " / " + nameEn + " / " + thumbnailUrl + " / " + profileUrl);

                MemberData memberData = new MemberData();
                memberData.setName(name);
                memberData.setThumbnailUrl(thumbnailUrl);
                memberData.setImageUrl(thumbnailUrl);
                memberData.setProfileUrl(profileUrl);
                memberList.add(memberData);
            }
        }
    }

    public HashMap<String, String> parseProfile(String response) {
        /*
        <div class="memberDetail">
            <div class="memberDetailPhoto">
                <img src="//cdn.akb48.co.jp/cache/image/?path=%2Fmembers%2Fprofile20150511%2Fteam_A_png%2Firiyama_anna.png" width="170" height="170" alt="入山 杏奈" />
            </div>
            <div class="memberDetailProfile">
                <p class="memberDetailProfileHurigana">イリヤマ アンナ</p>
                <h3 class="memberDetailProfileName">入山 杏奈</h3>
                <p class="memberDetailProfileEName">Anna Iriyama</p>
                <div class="memberDetailProfileWrapper">
                    <ul>
                        <li>
                            <h4 class="memberDetailProfileLeft">Office</h4>
                            <p class="memberDetailProfileRight">太田プロダクション</p>
                        </li>
                        <li>
                            <h4 class="memberDetailProfileLeft">Nickname</h4>
                            <p class="memberDetailProfileRight">あんにん</p>
                        </li>
                        <li>
                            <h4 class="memberDetailProfileLeft">Date of birth</h4>
                            <p class="memberDetailProfileRight">1995.12.03</p>
                        </li>
                        <li>
                            <h4 class="memberDetailProfileLeft">From</h4>
                            <p class="memberDetailProfileRight">Chiba</p>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        */
        //Log.i("##### response", response);

        HashMap<String, String> hashMap = new HashMap<>();

        Document doc = Jsoup.parse(response); // http://jsoup.org/

        Element root = doc.select(".memberDetail").first();
        if (root == null) {
            return hashMap;
        }

        String imageUrl = "";
        Element photo = root.select(".memberDetailPhoto > img").first();
        if (photo == null) {
            return hashMap;
        }
        imageUrl = "http:" + photo.attr("src");
        hashMap.put("imageUrl", imageUrl);

        Element profile = doc.select(".memberDetailProfile").first();
        if (profile == null) {
            return hashMap;
        }
        //Log.i("##### root", root.toString());

        Element el;

        el = profile.select(".memberDetailProfileHurigana").first();
        if (el == null) {
            return hashMap;
        }
        hashMap.put("furigana", el.text().trim());

        el = profile.select(".memberDetailProfileName").first();
        if (el == null) {
            return hashMap;
        }
        String name = el.text().trim();
        hashMap.put("name", name);

        el = profile.select(".memberDetailProfileEName").first();
        if (el == null) {
            return hashMap;
        }
        String nameEn = el.text().trim();
        hashMap.put("nameEn", nameEn);

        //Log.e(mTag, nameJa + " / " + nameEn);

        String html = "";
        Element subdetail = profile.select(".memberDetailProfileWrapper").first();
        if (subdetail != null) {
            Element ul = subdetail.select("ul").first();
            if (ul != null) {
                int count = 0;
                for (Element li : ul.select("li")) {
                    String title = li.child(0).text().trim();
                    String value = li.child(1).text().trim();
                    if (count > 0) {
                        html += "<br>";
                    }
                    html += title + "：" + value;
                    count++;
                }
            }
        }
        hashMap.put("html", html);

        hashMap.put("isOk", "ok");

        return hashMap;
    }
}