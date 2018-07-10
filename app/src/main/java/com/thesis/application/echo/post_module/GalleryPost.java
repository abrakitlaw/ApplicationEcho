package com.thesis.application.echo.post_module;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.thesis.application.echo.R;

/**
 * Created by Abra Kitlaw on 10-Jul-18.
 */

public class GalleryPost extends Fragment{

    View view;

    ImageButton selectYourImage;
    EditText edtTextShareYourStory;
    Button btnPostGallery;
    private static final int GALLERY_PICK = 1001;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_gallery, container, false);

        selectYourImage = view.findViewById(R.id.btnImageSelect);
        edtTextShareYourStory = view.findViewById(R.id.editTextShareYourStory);
        btnPostGallery = view.findViewById(R.id.btnPostGallery);
        
        selectYourImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select an Image"), GALLERY_PICK);
            }
        });
        
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_post, menu);
    }
}
