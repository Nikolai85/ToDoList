package com.example.todolist;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class NewItemFragment extends Fragment{

    private OnNewItemAddedListener onNewItemAddedListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Разметка
        View view = inflater.inflate(R.layout.new_item_fragment, container, false);

        final EditText myEditText = (EditText) view.findViewById(R.id.editText);
        myEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                    if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER) ||
                            (keyCode == KeyEvent.KEYCODE_ENTER)){
                        String newItem = myEditText.getText().toString();
                        onNewItemAddedListener.onNewItemAdded(newItem);
                        myEditText.setText("");
                        return true;
                    }
                return false;
            }
        });
        return view;
    }

    public interface OnNewItemAddedListener {
        void onNewItemAdded(String newItem);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onNewItemAddedListener = (OnNewItemAddedListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement OnNewItemAddedListener");
        }
    }
}
