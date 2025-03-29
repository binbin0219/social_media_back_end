package my_social_media_project_backend.demo.service;
import my_social_media_project_backend.demo.utility.BatikTranscoderUtils;
import org.apache.batik.transcoder.TranscoderException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.Buffer;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class AvatarService {

    private final WebClient webClient;
    private final R2StorageService r2StorageService;;

    public AvatarService(WebClient.Builder webClientBuilder, R2StorageService r2StorageService) {
        this.webClient = webClientBuilder.baseUrl("https://avataaars.io").build();
        this.r2StorageService = r2StorageService;
    }

    private static final List<String> MALE_TOP_TYPES = List.of("ShortHairDreads01", "ShortHairDreads02", "ShortHairFrizzle", "ShortHairShaggyMullet", "ShortHairShortCurly");
    private static final List<String> FEMALE_TOP_TYPES = List.of("LongHairBob", "LongHairBun", "LongHairCurly", "LongHairCurvy", "LongHairDreads", "LongHairFrida");

    private static final List<String> FACIAL_HAIR_TYPES = List.of("BeardMedium", "BeardLight", "Blank");
    private static final List<String> CLOTHE_TYPES = List.of("BlazerShirt", "BlazerSweater", "CollarSweater", "GraphicShirt", "Hoodie", "Overall");
    private static final List<String> EYE_TYPES = List.of("Wink", "Happy", "Default");
    private static final List<String> EYEBROW_TYPES = List.of("DefaultNatural", "Default", "RaisedExcited", "RaisedExcitedNatural");
    private static final List<String> MOUTH_TYPES = List.of("Smile", "Twinkle", "Default");
    private static final List<String> SKIN_COLORS = List.of("Light", "Brown", "DarkBrown");

    private final Random random = new Random();

    public String getRandomAvatarSvgFromAvatarIo(String gender) {
        String topType;

        if ("female".equalsIgnoreCase(gender)) {
            topType = getRandomElement(FEMALE_TOP_TYPES);
        } else {
            topType = getRandomElement(MALE_TOP_TYPES);
        }

        String facialHairType = getRandomElement(FACIAL_HAIR_TYPES);
        String clotheType = getRandomElement(CLOTHE_TYPES);
        String eyeType = getRandomElement(EYE_TYPES);
        String eyebrowType = getRandomElement(EYEBROW_TYPES);
        String mouthType = getRandomElement(MOUTH_TYPES);
        String skinColor = getRandomElement(SKIN_COLORS);

        String url = String.format(
                "/?avatarStyle=Circle&topType=%s&facialHairType=%s&clotheType=%s&eyeType=%s&eyebrowType=%s&mouthType=%s&skinColor=%s",
                topType, facialHairType, clotheType, eyeType, eyebrowType, mouthType, skinColor
        );

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block(); // Blocking because we're in a sync context, can be made reactive if needed
    }

    public String createAvatar(Integer userId, String gender) {
        try {
            String desiredPath = String.format("user/%d/avatar/avatar.png", userId);
            String avatarSvg = getRandomAvatarSvgFromAvatarIo(gender);
            byte[] avatarPng = BatikTranscoderUtils.convertSvgToPng(avatarSvg);
            return r2StorageService.uploadFile(desiredPath, avatarPng);
        } catch (TranscoderException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T getRandomElement(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
}

