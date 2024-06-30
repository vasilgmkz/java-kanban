package converter;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        if (duration == null) {
            jsonWriter.value("null");
            return;
        }
        long durations = duration.toMinutes();
        jsonWriter.value(durations);
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        String duration = jsonReader.nextString();
        if (duration.equals("null")) {
            return null;
        }
        return Duration.ofMinutes(Long.parseLong(duration));
        //return Duration.ofMinutes(Long.parseLong(jsonReader.nextString()));
    }

    public static class SubtitleListTypeToken extends TypeToken<Duration> {
    }
}
