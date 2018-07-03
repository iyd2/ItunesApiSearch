package iyd2.projects.itunessearchapi;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

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
    private String price;
    private String currency;
    private String contentAdvisoryRating;
    private String collectionExplicitness;
    private String trackCount;
    private String copyright;
    private String country;
    private String genre;
    private Date releaseDate;
    private List<String> listOfSongs;

    /**
     * Album constructor.
     */
    public AlbumItem(String collectionId, String collectionName, String artistName, String imageUrl) {
        this.collectionId = collectionId;
        this.collectionName = collectionName;
        this.artistName = artistName;
        this.imageUrl = imageUrl;
    }

    // Getters and setters.

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
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

    public String getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(String trackCount) {
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

    public List<String> getListOfSongs() {
        return listOfSongs;
    }

    public void setListOfSongs(List<String> listOfSongs) {
        this.listOfSongs = listOfSongs;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * Setter that parses release date from given String.
     */
	public void setReleaseDate(String releaseDateString) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            this.releaseDate = format.parse(releaseDateString);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Returns concatenated by blank String of price and currency.
     */
    public String getPriceWithCurrency() {
        return new StringBuilder(getTextValue(this.price)).append(" ").append(getTextValue(this.currency)).toString();
    }

    /**
     * Returns String value safely.
     */
    private String getTextValue(String value) {
        return value == null ? "" : value;
    }

    /**
     * Returns release date without time as String.
     */
    public String getDisplayedDateString() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        if (this.getReleaseDate() != null) {
            return new StringBuilder("Release: ").append(format.format(this.getReleaseDate())).toString();
        } else {
            return "";
        }
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
}
