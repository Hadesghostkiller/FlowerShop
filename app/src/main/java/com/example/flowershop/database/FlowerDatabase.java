package com.example.flowershop.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.flowershop.database.dao.CardTemplateDao;
import com.example.flowershop.database.dao.CartDao;
import com.example.flowershop.database.dao.ChatbotResponseDao;
import com.example.flowershop.database.dao.FlowerDao;
import com.example.flowershop.database.dao.OrderDao;
import com.example.flowershop.database.dao.OrderDetailDao;
import com.example.flowershop.database.entity.CardTemplate;
import com.example.flowershop.database.entity.Cart;
import com.example.flowershop.database.entity.ChatbotResponse;
import com.example.flowershop.database.entity.Flower;
import com.example.flowershop.database.entity.Order;
import com.example.flowershop.database.entity.OrderDetail;

import java.util.List;

@Database(
    entities = {Flower.class, Cart.class, Order.class, OrderDetail.class, CardTemplate.class, ChatbotResponse.class},
    version = 3,
    exportSchema = false
)
public abstract class FlowerDatabase extends RoomDatabase {
    public abstract FlowerDao flowerDao();
    public abstract CartDao cartDao();
    public abstract OrderDao orderDao();
    public abstract OrderDetailDao orderDetailDao();
    public abstract CardTemplateDao cardTemplateDao();
    public abstract ChatbotResponseDao chatbotResponseDao();

    private static volatile FlowerDatabase INSTANCE;

    public static FlowerDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (FlowerDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        FlowerDatabase.class,
                        "flowershop_db"
                    )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }

    public void populateInitialData() {
        FlowerDao flowerDao = flowerDao();
        CardTemplateDao cardTemplateDao = cardTemplateDao();
        ChatbotResponseDao chatbotResponseDao = chatbotResponseDao();

        List<Flower> flowers = flowerDao.getAllFlowersSync();
        if (flowers == null || flowers.isEmpty()) {
            flowerDao.insert(Flower.createWithoutID("Hoa Hong Do", 350000, "hoa_hong", "Hoa Bo", 20));
            flowerDao.insert(Flower.createWithoutID("Hoa Lan Trang", 450000, "hoa_lan", "Hoa Bo", 15));
            flowerDao.insert(Flower.createWithoutID("Hoa Cuc Vang", 280000, "hoa_cuc", "Hoa Bo", 25));

            flowerDao.insert(Flower.createWithoutID("Hoa Sinh Nhat", 500000, "hoa_sinh_nhat", "Sinh Nhat", 30));
            flowerDao.insert(Flower.createWithoutID("Bo Hoa Chuc Mung", 650000, "hoa_chucmung", "Sinh Nhat", 20));
            flowerDao.insert(Flower.createWithoutID("Hoa Huong Duong", 420000, "hoa_huongduong", "Sinh Nhat", 18));

            flowerDao.insert(Flower.createWithoutID("Khai Truong Phat", 1200000, "hoa_kt1", "Khai Truong", 10));
            flowerDao.insert(Flower.createWithoutID("Chuc Mung Khai Truong", 1500000, "hoa_kt2", "Khai Truong", 8));
            flowerDao.insert(Flower.createWithoutID("Hoa Chuc Moc", 980000, "hoa_chucmo", "Khai Truong", 12));

            flowerDao.insert(Flower.createWithoutID("Vong Hoa Chia Buon", 800000, "hoa_cuoi1", "Chia Buon", 10));
            flowerDao.insert(Flower.createWithoutID("Hoa Tang Trieu", 650000, "hoa_tangtrieu", "Chia Buon", 8));
            flowerDao.insert(Flower.createWithoutID("Hoa Phong Su", 550000, "hoa_phongsu", "Chia Buon", 12));

            flowerDao.insert(Flower.createWithoutID("Binh Hoa Cuoi", 2500000, "hoa_cuoi", "Hoa Cuoi", 5));
            flowerDao.insert(Flower.createWithoutID("Bo Hoa Cuoi Dep", 1800000, "hoa_cuoi2", "Hoa Cuoi", 7));
        }

        List<CardTemplate> templates = cardTemplateDao.getAllTemplates();
        if (templates == null || templates.isEmpty()) {
            cardTemplateDao.insert(new CardTemplate("Sinh Nhat",
                "Gửi {ten},\n\nChúc mừng sinh nhật! Mong bạn luôn vui vẻ, hạnh phúc và thành công.\n" +
                "Tuổi mới thật nhiều sức khỏe và niềm vui!\n\n{message}\n\nNgười gửi: {nguoi_gui}"));

            cardTemplateDao.insert(new CardTemplate("Khai Truong",
                "Kính gửi {ten},\n\nChúc mừng khai trương! Chúc cửa hàng luôn đông khách, buôn may bán đắt.\n" +
                "Phát tài phát lộc, thuận buồm xuôi gió!\n\n{message}\n\nNgười gửi: {nguoi_gui}"));

            cardTemplateDao.insert(new CardTemplate("Chia Buon",
                "Gửi {ten},\n\nVô cùng thương tiếc trước nỗi mất mát của gia đình bạn.\n" +
                "Xin gửi lời chia buồn sâu sắc nhất.\n\n{message}\n\nNgười gửi: {nguoi_gui}"));

            cardTemplateDao.insert(new CardTemplate("Hoa Cuoi",
                "Gửi {ten},\n\nChúc mừng hai bạn đã tìm được bến đỗ cuối cùng!\n" +
                "Chúc hai bạn trăm năm hạnh phúc, son sắt nghĩa tình.\n\n{message}\n\nNgười gửi: {nguoi_gui}"));
        }

        List<ChatbotResponse> responses = chatbotResponseDao.getAllResponses();
        if (responses == null || responses.isEmpty()) {
            chatbotResponseDao.insert(new ChatbotResponse("sinh nhat",
                "Với dịp sinh nhật, shop gợi ý bạn chọn:\n1. Hoa Sinh Nhat (500k)\n2. Bo Hoa Chuc Mung (650k)\n3. Hoa Huong Duong (420k)\nCác loại hoa này tượng trưng cho sự vui vẻ, tươi sáng!"));

            chatbotResponseDao.insert(new ChatbotResponse("khai truong",
                "Khai trương nên chọn hoa sang trọng nhé! Shop gợi ý:\n1. Khai Truong Phat (1.2tr)\n2. Chuc Mung Khai Truong (1.5tr)\n3. Hoa Chuc Moc (980k)\nChúc đối tác làm ăn phát đạt!"));

            chatbotResponseDao.insert(new ChatbotResponse("chia buon",
                "Gửi lời chia buồn, shop có:\n1. Vong Hoa Chia Buon (800k)\n2. Hoa Tang Trieu (650k)\n3. Hoa Phong Su (550k)\nXin chia sẻ với mất mát của bạn."));

            chatbotResponseDao.insert(new ChatbotResponse("hoa cuoi",
                "Chúc mừng đám cưới! Shop có:\n1. Binh Hoa Cuoi (2.5tr)\n2. Bo Hoa Cuoi Dep (1.8tr)\nHoa tươi thắm cho ngày vui!"));

            chatbotResponseDao.insert(new ChatbotResponse("hoa bo",
                "Hoa bó đẹp nhất tại shop:\n1. Hoa Hong Do (350k)\n2. Hoa Lan Trang (450k)\n3. Hoa Cuc Vang (280k)\nTươi lâu, thơm ngát!"));

            chatbotResponseDao.insert(new ChatbotResponse("xin chao|hello|hi|chao",
                "Xin chào! Tôi là trợ lý ảo của FlowerShop. Tôi có thể giúp bạn:\n1. Tư vấn chọn hoa theo dịp\n2. Viết thiệp tự động\n3. Tra cứu thông tin hoa\nBạn cần hỗ trợ gì?"));

            chatbotResponseDao.insert(new ChatbotResponse("cam on|thanks",
                "Rất vui được hỗ trợ bạn! Chúc bạn một ngày tốt lành."));

            chatbotResponseDao.insert(new ChatbotResponse("gia|price|bao nhieu",
                "Bạn có thể xem giá hoa trong mục 'Cửa hàng'. Giá dao động từ 280k đến 2.5tr tùy loại nhé!"));
        }
    }
}