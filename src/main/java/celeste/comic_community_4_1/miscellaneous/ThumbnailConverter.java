package celeste.comic_community_4_1.miscellaneous;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

public class ThumbnailConverter {

    public static final String DEFAULT_AVATAR = "/9j/4AAQSkZJRgABAgAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAGQAZADASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwB9FFFeofnwUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRViysrnUbpba0heWViOFGdoJxk+g9zRsNJydkV6s2mn3uoHFnaT3HzbSY0JAPueg/Gu/0TwBbW2ybVXFzMrZ8pD+69s5GW/l7V2EFvDbRLFBEkUa9EjUKB+ArnniEvhPaw+TVJrmqvl8up5pbfDzVpShnntYEPLDJdh7YAx+tbcXw304IBNfXbP6x7VH5EGu0orB15vqerTyrCw+zf1OQHw50YdZ75vrKv8ARaG+HOjnpcXy/SRT/Na6+il7Wfc1/s/C/wAiOJuPhvZmMi1v7hH7GYK4/QCsK78AazbRvJG1vcheixMQxH0Ix+tep0VSrzRjUynCz2VvQ8JuLS5s5BHdW80DkZCyoVJ+metQ17pd2VrfReVd28U6ZyFkQMAfUZ6GuI1r4fYDz6PIc5LG3mbt1whx+AB/Ot4YhPR6HkYnJ6tNc1N8y/E4KipJoJraZobiJ4pV+8jrgj8Kjrc8hprRhRRRQIKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiitXQNDn17UFgjysKYaaTptXIzg4I3HnAPp7GhtJXZdOnKpJQirtjtB8P3evXYSJSluh/ezMDtAyMgHGC2DnFeraVo9no9oILOIKMDc55Zz6se9S6fp9tpllHa2sYSNABwACxxjccdSccmrVcFSq5vyPrsDgIYaN3rLv/kFFFFZHoBRRRQAUUUUAFFFFABRRRQBja94cs9et8SgR3Cj93OijcMZwCe65PT+VeU6ppV3o94bW8TDjlWH3XHqp9K9vrN1rRrbW7BrW4GG6xyAco3qP6jvW1Kq4aPY8vH5dHELnhpL8zxWirN/YXOmX0tndxlJYz1xgOOcMvqpwcH2PcGq1dyd9UfKSi4txkrNBRRRQSFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAS21vJd3UVtCAZZXCID3Jr2PQNGi0TSYrRMGQ/NM4Odzkcn6dh7AVyXw80gSSTavLghCYYVK9DwWYfy4/wBoV6DXHiJ3fKj6bJ8IoQ9tLd7egUUUVznthRRRQAUUUUAFFFFABRRRQAUUUUAFFFFAHM+M/Dw1jSnuLaNf7Qt13xsEBaRRkmMnrg5OPfHvXk0b+YD8rKynDIwwVPoRXv5rxz4g6M2i+IRqNrE4tr3LuTyvm5O4e2eD9d1dNCpb3WeFm+DUl7aO/UxqKjhmWaPcv4j0qSus+basFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAU5EeWRY41LSOQqqO5PQU2tjwrZNfeJrFB92KQTMfZOf5gUm7K5pSg6k1BdWer6TYjTdJtbMBQYolVigwC2PmP4nJq7RRXmN31Pu4xUYqK2QUUUUFBRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABXNeO9JTVfCd2CWD2oN1HjuUByPxBYfjXS010WRGR1DKwwQRkEelNOzuRUgqkHB9T5rhmaFw6H8OxrWhmWZNy/iPSqOqWqWOr31nHnZb3EkS59FYgfyqvFK0L70/EetehFnxVSnrbqbVFRwzLMm5fxHpUlWc2wUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFdb8O1z4jnPZbRv1dP/r1yVdl8NwP7Zvj3Fuv/AKFUVfgZ25er4qHqelUUUV5x9oFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQB4T47tBZ+M9RC8LK6yj/AIEoJ/XNc5XZfE4AeLsjvboT+tcbXdD4UfI4pWrzS7sfFK0L70P1HrWtDMsybl/EelY1PilaFw6fiPWrTscs4cxtUVHDMs0e5fxHpUlWczVgooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigArr/hy+Nfuk/vWpP5Mv8AjXIV0ngW7S18Txq5A8+NoQT68N/7LUVVeDOvASUcTBvuesUUUV5x9sFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQB4l8R5xN4yuFBz5UcaH/AL5z/WuTrU8SzNceKdWlZt2buRQc9g20foBWXXdFWij4/ES5qspebCiiiqMh8UrQyb069x61rQzLMm5fxHpWNT4pWhfen4j1pp2M5w5jaoqOGZZk3L+I9Kkqzn2CiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKnsbkWWoW12QSIJUlwOpwc4/pUFFA02ndHvMM0dxBHNEwaORQ6sO4IyDT65bwJqv2/RBayunnWh8sKDz5eBtOPzH4V1NebKPK2j7rD1lWpRqLqFFFFSbBRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABWfrt+dM0K/vQyq8MDsm48F8fKPxOB+NaFea/FXWVENroqbw7EXMpBwCoyFU+vOT/wEVUI80rHPiqqpUpTPMCzMdzsWY8lj1J9aKKK7j5IKKKKACiiigB8UrQvvQ/Ueta0MyzJuX8R6VjU+KVoXDp+I9aadiJw5jaoqOGZZo9y/iPSpKs5mrBRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAanh/WH0PV47tQDG37uYYyTGSCce/GRXssUsc8KSxOHjdQyMpyGB5BFeDV23gjxL9mlGlXshMMh/0eR2J2N02ey+nv8AXjnr0+ZcyPaynGqlL2M9nt6/8E9GooorjPpwooooAKKKKACiiigAooooAKKKKACiimySJDG8kjqkaAszMcBQOpJoAq6pqVvpGmz310wWKFC2MgFjjhRk9T0A9a+fdU1GfVtTuL64ZjJM5fBYnYM8KPYDgfSuk8eeKx4gvhaWhP8AZ9s52ngiVwSN4IJ+XHT2JPfjkK66UOVXZ83mOK9tPkjsgooorU84KKKKACiiigAooooAfFK0Mm9Ovceta0MyzJuX8R6VjU+KVoX3p+I9aadjOcOY2qKjhmWZNy/iPSpKs59gooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiip7azur1itpazXDA4IiQtj646fjQNJt2RBRXT23gHW7iJZG+zW+f4ZZDuH/fIP8AOtGD4bXBH+kanEp9I4if5kVm6sF1OyGXYqe0H+X5nD0V12q+ANQs43ms5ku40XcUClZPwHIP559q5J0aORo3VkdDtZWGCp9COxqozjLYwrYerQdqkbHf+E/GSsiafqsuHHyxXDHgjsHPr79+/PXvK8DIBBBGQa6Xw/42udDUQXwkutPGTx80sXTAGTgoADx1GeDjisKtDrE9nL81slSrv0f+Z6vRVPTdUsdXtVubC6iniIGSjAlSRnDDseeh5q5XIfQppq6CiiigYUUUUAFFFFABRRWXrXiHTNBt2lvrlVcKWSFSDJJ/ur39M9PU00r7EylGKvJ2RoyyxwwvLLIscaKWd3OAoHJJPYV5H438cvqryaZpcjJYqSssqnBnIyCAQcGMjH1+nXN8UeN9Q8Qu8EZa20/cdsKnDSL28zHX6dPrjNcvXRTpW1Z4WNzH2n7ult3Cipra0ub2dYLS3lnmbpHEpZj+Aru9N+FV9PCJNQv4rVmUERxoZCPZjkAH6Z+tayko7nn0sPVrfArnn1Feh3PwmvlybXVbd/QSxMn6gmsjUPhz4isIDKIYbsDqtq5Zh+DAE/hmkqkX1LlgsRHVxZydFS3NrcWc3k3VvNby43eXMhRseuDzUVWczTWjCiiigAooooAKKKKAHxStC+9D9R61rQzLMm5fxHpWNT4pWhcOn4j1pp2InDmNqio4Zlmj3L+I9KkqzmasFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFSQwTXMyw28TyytwqIMk061tZr26itrdC8srBVUev+Hv2r1nw34at9BtASFkvXH72Xg44GVU4B25H4/yzqVFBeZ3YLAzxUtNIrdmDofw/RAs+rvukDBlgib5cejHHP0HHua7W2tbezi8q2gihjznZGgUZ+gqaiuKU5S3Pq8PhaWHVqa/zCiiioOgKxNc8L2GuR5kXybgcieNRuPGAG/vD2/UVt0U02ndEVKUKseWaujxjWvD1/ocu25TfEfuzxg7Dz0z2Pt/OsqveZYY54nimjWSNxhkcZDD0Irida+H0crNPpMoidnLNDKfkAPZSBx9P5V108QnpI+dxeTzh71DVduv/BPNVlvdLuPtmm3M1u/G7ymIzg55HQj2PFdhpHxVuEbZrFmsqBeJbUYcn3Vjg/UEfSsG/wBLvtMcreWksIDbQ7qdrH2boaxLu025kjHHUqO3uKuUIy1OKhiq2HfLex7Tpvjnw9qUPmDUYrZh1S7YREfmcH8Ca3be8tryPzLa4inT+9E4YfpXzZSxO0D74mMbeqHB/SsXQXRnqQzea+ONz6Yor51XXNXjGE1a/Qei3Lj+tI+tarKMSapfOPR7hz/M1PsH3Nv7Xj/KfQV1qNjYruvLy3tx6zSqn8zWBqnxA8PaYBi7N45/hswJP/Hshf1rw5z5j73+Zv7x5NFUqC6swnm9R/BFL8TvdZ+KGoXTSw6XAlpbspUSP80v1HOB+v1rh7m5uLycz3U8s8xGDJK5ZiPqairV0jw3q2uSxrZWchjf/l4dSsS/Vun4DJ9q1UYxRwTq1sRKzbbMqum8N+CNU19kmMZtbHKkzSggup5+QY+bjv09+1dz4d+Gtjp2y51ZlvboA5ixmFenYjLHryeOeldyqqihVUKqjAAGABWM63SJ6WGytv3q33GTofhrTPD0ASxtx5hGHnfBkf6tjpx0GB7Vr0UVg23ue1GEYLlirIKKKKRRTv8AS7DVIvKv7OC4QZx5iAlfcHqD7ivNfEnwyltEe70SR54UUs9tId0nrhCB83HY8+56V6tRVRm47HPXwtKurSWvc+aHR4pHjkRkkRirIwwVI4IIPQ02vYfG/giLV4X1HTY1j1FAWdFGBcAckED+P0P4HsR5A6PFI0ciMkiEqyOMMpHBBHYiuuE1JHzeJw08POz26MbRRRVnMFFFFABRRRQA+KVoZN6de49a1oZlmTcv4j0rGp8UrQvvT8R6007Gc4cxtUVHDMsybl/EelSVZz7BRRRQAUUUUAFFFFABRRRQAUE4GT0ore8I6MNY1tBKp+z2+JZPlyrYPCHtye3cA0pSUVdmlKlKrNQjuzsPBHh3+zrT+0blR9quFBQZPyRkAgY7Me/4e9ddRRXnSk5O7Pt8PQjQpqnDoFFFFSbBRRRQAUUUUAFFFFAEc0EVzE0U8SSxtwyOoYH6g1zuo+BdHv5PMjSS0bHS3IVT/wABII/LFdNRVRk47Myq0KVVWqRTPJ9R+FWpC4Z9PvLR4Tk7ZdyMPpgEfyrnb3wT4jsWw2lTTAfxQYkB/I5/SveqK0VaXU4Z5VQfw3R87/8ACP63/wBATU//AADk/wAKB4e1wnA0TUv/AAEkH9K+iKKft32M/wCyIfzM8KsfAfiO+I/4lzW6/wB64YJ+nX9K6DTvhRetNnU9Qt44h/DbZdj+LAAfka9VoqXWkzaGV0I73Zy+leAPD+lbibX7a5/jvMSY+gwF/HGa6ZEWNFRFCoowqqMAD0FOorNtvc7adKFNWgrBRRRSNAooooAKKKKACiiigArzH4k+FlQDW7CDqcXUcUf1PmnH5E/Q+tenVHPBFdW8tvOgeKVCjoejKRgg/hVRk4u5hiKEa9NwZ81UVqeItFl0DXLmwcN5atuhYnJeIk7TnuccH3BrLrtTurnyc4uEnGW6CiiimSFFFFABRRRQA+KVoX3ofqPWtaGZJk3L+I9KxqfFK0L7l/EetNOxnOHMbVFFFWc4UUUUAFFFFABRRRQAZwMnivW/Bulf2b4ehZjmW6xO+R03AYX8Bj8c15podidR1uztfL8xHlUyAjjYDls/gDXtagKAAAAOABXNiZbRPeySheUqz6aIWiiiuQ+jCiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooA4H4oaILrSYdVhhLXFswSRlHPlHPX1w2D7AtXklfSOoWaahp1zZSkiO4iaJivUBgQcfnXzpd2z2V7PaSkGSCRonI6FlJBx+IrqoyurHz+a0eWoqi6kNFFFbHlBRRRQAUUUUAFFFFAG7RRRWhxhRRRQAUUUUAFFFFAHafDmyd9Tur0j93HF5Q92Yg/oF/WvSK5T4fWzweHGkcY8+dpFz6ABf/ZTXV1wVnebPsssp+zwsfPX7wooorI7wooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACvEPiJpseneL5jFwLuNbkj0JJB/VSfxr2+vNvizp6G30/Uxw6ubc+4ILD8trfnWtF2kefmdPnoN9tTy+iiius+aCiiigAooooAKKKKAN2iiitDjCiiigAooooAKCcAk9qKlt4DdXUNuOsrrGPxOKBpNuyPZfD8fleHdNQrtP2aMsPcqCf1zWlTY0WONUUYVQAB7CnV5jd3c+9hHlio9gooopFhRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFcl8SLA3vg+Z1+9bSJOPwOD+jGutrL8SoZPC2rIBljZzYHvsOKqLtJMxxEVOlKL7M+eqKQHIB9aWu4+QCiiigAooooAKKKKAN2iiitDjCiiigAooooAKvaKCfEGmAf8/cX/oYqjWp4aj8zxRpi+k4b8gT/SlLZmtFXqxXmvzPaKKKK8w+8CiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACo7iEXFvLC3SRCp/EYqSigHqfM7RmFjGeqHafwpKu6wnla3qEY/guZF/JiKpV6CPi5KzaCiiigQUUUU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";

    private static final double LENGTH = 200.0;
    private static final double SQUARE_LENGTH = 400.0;

    private static BufferedImage scale(BufferedImage source, double ratio) {
        int w = (int) (source.getWidth() * ratio);
        int h = (int) (source.getHeight() * ratio);
        w = (w > 1.25 * h) ? (int) (1.25 * h) : w;
        h = (h > 1.25 * w) ? (int) (1.25 * w) : h;
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bi.createGraphics();
        double xScale = (double) w / source.getWidth();
        double yScale = (double) h / source.getHeight();
        AffineTransform at = AffineTransform.getScaleInstance(xScale, yScale);
        g2d.drawRenderedImage(source, at);
        g2d.dispose();
        return bi;
    }

    public static BufferedImage convert(BufferedImage source) {
        double ratio = 1.0, width = source.getWidth(), height = source.getHeight();
        if (width >= LENGTH && height >= LENGTH) {
            if (width > height) {
                ratio = LENGTH / height;
            } else {
                ratio = LENGTH / width;
            }
        }
        return scale(source, ratio);
    }

    public static BufferedImage convertSquare(BufferedImage source) {
        double ratio = 1.0, width = source.getWidth(), height = source.getHeight();

        if (width > height) {
            source = source.getSubimage(0, 0, (int) height, (int) height);
            width = height;
        } else {
            source = source.getSubimage(0, 0, (int) width, (int) width);
        }

        if (width > SQUARE_LENGTH) {
            ratio = SQUARE_LENGTH / width;
        }
        return scale(source, ratio);
    }

    public static BufferedImage convertSquare(BufferedImage source, double squareSize) {
        double ratio = 1.0, width = source.getWidth(), height = source.getHeight();

        if (width > height) {
            source = source.getSubimage(0, 0, (int) height, (int) height);
            width = height;
        } else {
            source = source.getSubimage(0, 0, (int) width, (int) width);
        }

        if (width > squareSize) {
            ratio = squareSize / width;
        }
        return scale(source, ratio);
    }

    public static String toBase64(BufferedImage source, String type) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(source, type, bos);
        byte[] data = bos.toByteArray();
        return Base64.getEncoder().encodeToString(data);
    }

    @Deprecated
    public static String[] toBase64(MultipartFile f) throws Exception {
        String type = f.getOriginalFilename().substring(f.getOriginalFilename().lastIndexOf(".") + 1);
        File convFile = new File(f.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(f.getBytes());
        fos.close();

        BufferedImage bImage = ImageIO.read(convFile);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, type, bos);
        final String base64 = Base64.getEncoder().encodeToString(bos.toByteArray());
        bos.close();
        convFile.delete();

        BufferedImage thumbnailBI = convert(bImage);
        bos = new ByteArrayOutputStream();
        ImageIO.write(thumbnailBI, type, bos);
        final String thumbnail = Base64.getEncoder().encodeToString(bos.toByteArray());
        bos.close();
        return new String[]{base64, thumbnail};
    }

    public static String toBase64Only(MultipartFile f) throws Exception {
        String type = f.getOriginalFilename().substring(f.getOriginalFilename().lastIndexOf(".") + 1);
        File convFile = new File(f.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(f.getBytes());
        fos.close();

        BufferedImage bImage = ImageIO.read(convFile);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, type, bos);
        final String base64 = Base64.getEncoder().encodeToString(bos.toByteArray());
        bos.close();
        convFile.delete();

        return base64;
    }

    public static String toBase64Square(MultipartFile f) throws Exception {
        String type = f.getOriginalFilename().substring(f.getOriginalFilename().lastIndexOf(".") + 1);
        File convFile = new File(f.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(f.getBytes());
        fos.close();

        BufferedImage bImage = ImageIO.read(convFile);
        bImage = convertSquare(bImage);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, type, bos);
        final String base64 = Base64.getEncoder().encodeToString(bos.toByteArray());
        bos.close();
        convFile.delete();

        return base64;
    }

    public static String toBase64Square(MultipartFile f, double squareSize) throws Exception {
        String type = f.getOriginalFilename().substring(f.getOriginalFilename().lastIndexOf(".") + 1);
        File convFile = new File(f.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(f.getBytes());
        fos.close();

        BufferedImage bImage = ImageIO.read(convFile);
        bImage = convertSquare(bImage, squareSize);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, type, bos);
        final String base64 = Base64.getEncoder().encodeToString(bos.toByteArray());
        bos.close();
        convFile.delete();

        return base64;
    }


}
