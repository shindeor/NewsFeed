package eu.booksbnb.newsfeed;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

public class NewsAdapter extends ArrayAdapter<Article> {
    Article currentArticle;
    View listItemView;

    public NewsAdapter(Activity context, int simple_list_item, ArrayList<Article> articles) {
        super(context, 0, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        currentArticle = getItem(position);
        TextView categoryView = (TextView) listItemView.findViewById(R.id.category);
        TextView titleView = (TextView) listItemView.findViewById(R.id.titleV);
        TextView authorView = (TextView) listItemView.findViewById(R.id.author);
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        ImageView thumbView = (ImageView) listItemView.findViewById(R.id.thumbnail);
        categoryView.setText(currentArticle.getCategory());
        titleView.setText(currentArticle.getNewsTitle());
        //Get authors and narrow down to just the first 2, if there are more, add "..."
        String[] authors = currentArticle.getAuthor();
        String authorFinal = "";
        if (authors.length > 0) {
            if (authors.length > 1) {
                for (int ith = 0; ith < authors.length; ith++) {
                    authorFinal += authors[ith];
                    if (ith < 1 && ith < authors.length - 1) {
                        authorFinal += ", ";
                    }
                    if (ith == 1) {
                        if (ith != authors.length) {
                            authorFinal += "...";
                        }
                        break;
                    }
                }
            } else {
                authorFinal = authors[0];
            }
        }
        authorView.setText(authorFinal);
        dateView.setText(currentArticle.getWritDate());
        thumbView.setImageBitmap(currentArticle.getThumbnail());



        return listItemView;

    }
}
