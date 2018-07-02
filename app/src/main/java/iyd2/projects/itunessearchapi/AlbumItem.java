package iyd2.projects.itunessearchapi;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Album entity class.
 */
public class AlbumItem implements Comparable<AlbumItem>, Serializable {
    private static final String TAG = "AlbumItem";
    private String jsonData;
    private String collectionId;
    private String collectionName;
    private String artistName;
    private String imageUrl;
    private int price;
    private String currency;
    private String contentAdvisoryRating;
    private String collectionExplicitness;
    private int trackCount;
    private String copyright;
    private String country;
    private String genre;
    private Date releaseDate;

    private List<String> listOfSongs;

    public AlbumItem(String collectionId, String collectionName, String artistName, String imageUrl) {
        this.collectionId = collectionId;
        this.collectionName = collectionName;
        this.artistName = artistName;
        this.imageUrl = imageUrl;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public AlbumItem(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getContentAdvisoryRating() {
        return contentAdvisoryRating;
    }

    public void setContentAdvisoryRating(String contentAdvisoryRating) {
        this.contentAdvisoryRating = contentAdvisoryRating;
    }

    public String getCollectionExplicitness() {
        return collectionExplicitness;
    }

    public void setCollectionExplicitness(String collectionExplicitness) {
        this.collectionExplicitness = collectionExplicitness;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<String> getListOfSongs() {
        return listOfSongs;
    }

    public void setListOfSongs(List<String> listOfSongs) {
        this.listOfSongs = listOfSongs;
    }

    public boolean fillFromJson() {

        if (jsonData == null) {
            return false;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            String imageUrl = jsonObject.getString("artworkUrl100").replace("100x100bb", "600x600bb");

            if (!isUrlValid(imageUrl)) {
                return false;
            }

            this.collectionId = jsonObject.getString("collectionId");
            this.collectionName = jsonObject.getString("collectionName");
            this.artistName = jsonObject.getString("artistName");
            this.imageUrl = imageUrl;

        } catch (JSONException e) {
            return false;
        }

        return true;
    }

    public boolean fillFromJsonFull() {
        if (!fillFromJson()) {
            return false;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            this.price = jsonObject.getInt("collectionPrice");
            this.currency = jsonObject.getString("currency");
            this.contentAdvisoryRating = jsonObject.getString("contentAdvisoryRating");
            this.collectionExplicitness = jsonObject.getString("collectionExplicitness");
            this.trackCount = jsonObject.getInt("trackCount");
            this.copyright = jsonObject.getString("copyright");
            this.country = jsonObject.getString("country");
            this.genre = jsonObject.getString("primaryGenreName");

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            this.releaseDate = format.parse(jsonObject.getString("releaseDate"));
            //TODO:releaseDate
        } catch (JSONException e) {
            return false;
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    @Override
    public int compareTo(@NonNull AlbumItem albumItem) {
        return collectionName.compareTo(albumItem.collectionName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlbumItem albumItem = (AlbumItem) o;
        return Objects.equals(collectionId, albumItem.collectionId)      &&
                Objects.equals(collectionName, albumItem.collectionName) &&
                Objects.equals(artistName, albumItem.artistName)         &&
                Objects.equals(imageUrl, albumItem.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collectionId, collectionName, artistName, imageUrl);
    }

    private boolean isUrlValid(String urlSpec) {

        try {
            new URL(urlSpec);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
