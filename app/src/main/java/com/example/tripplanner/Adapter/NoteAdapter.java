package com.example.tripplanner.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripplanner.R;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.viewHolder> {

    List<String> notesList;
    Context context;
    int id;

    public NoteAdapter(List<String> notesList, Context context) {
        this.context = context;
        this.notesList = notesList;
    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_row, parent, false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        String note=notesList.get(position);
        holder.getEditText().setText(note);
        holder.note.setOnClickListener(v -> id = holder.getAdapterPosition());
    }



    @Override
    public int getItemCount() {
        return notesList.size();
    }
    public void removeItem(){
        notesList.remove(notesList.get(id));
        notifyItemRemoved(id);
        Toast.makeText(context, "z" + id, Toast.LENGTH_SHORT).show();

    }
    public class viewHolder extends RecyclerView.ViewHolder {
        EditText note;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            note = itemView.findViewById(R.id.editText);
            note.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    notesList.set(getAdapterPosition(), s.toString());
                }



                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }
        public EditText getEditText() {
            return note;
        }

    }
}