package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        if (duration == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(duration.toString());
        }
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        String text = jsonReader.nextString();
        if (text.equals("null")) {
            return null;
        }
        return Duration.parse(text);
    }
}