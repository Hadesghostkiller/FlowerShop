package com.example.flowershop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class FlowerShopLogicSystem {
    static Scanner sc = new Scanner(System.in);
    static ArrayList<User> userList = new ArrayList<>();
    static ArrayList<CartItem> tempCart = new ArrayList<>();
    static User currentUser = null;

    // --- 1. MODEL DU LIEU ---
    static class User {
        String u, p, ph;
        User(String u, String p, String ph) { this.u = u; this.p = p; this.ph = ph; }
    }

    static class Flower {
        String id, name, category, tag; double price;
        Flower(String i, String n, double p, String c, String t) {
            this.id = i; this.name = n; this.price = p; this.category = c; this.tag = t;
        }
    }

    static class CartItem {
        Flower flower; int qty; String bannerMsg;
        CartItem(Flower f, int q, String m) { this.flower = f; this.qty = q; this.bannerMsg = m; }
    }

    // --- 2. KHOI TAO DATA  ---
    static ArrayList<Flower> initProductData() {
        ArrayList<Flower> data = new ArrayList<>();
        // Hoa chuc mung (Them nhieu data hon)
        data.add(new Flower("CM1", "Ke Khai Truong Hong Phat", 1200000, "Hoa chuc mung", "HOT"));
        data.add(new Flower("CM2", "Ke Hoa Thinh Vuong", 1500000, "Hoa chuc mung", "NEW"));
        data.add(new Flower("CM3", "Ke Hoa Khoi Dau Moi", 950000, "Hoa chuc mung", ""));
        data.add(new Flower("CM4", "Ke Chuc Mung Mini", 500000, "Hoa chuc mung", ""));

        // Hoa sinh nhat
        data.add(new Flower("SN1", "Bo Hong Do Ecuador", 850000, "Hoa sinh nhat", "HOT"));
        data.add(new Flower("SN2", "Gio Hoa Nang Thuy Tinh", 450000, "Hoa sinh nhat", ""));
        data.add(new Flower("SN3", "Bo Hoa Huong Duong Tuoi", 350000, "Hoa sinh nhat", "NEW"));
        data.add(new Flower("SN4", "Bo Hoa Tulip Ha Lan", 1100000, "Hoa sinh nhat", "HOT"));

        // Chia buon
        data.add(new Flower("CB1", "Ke Chia Buon Thanh Kinh", 800000, "Chia buon", ""));
        data.add(new Flower("CB2", "Vong Hoa Co Dien", 650000, "Chia buon", ""));
        data.add(new Flower("CB3", "Ke Hoa Vinh Hang", 1800000, "Chia buồn", "HOT"));

        // Hoa bo
        data.add(new Flower("HB1", "Bo Hoa Baby Trang", 250000, "Hoa bo", "NEW"));
        data.add(new Flower("HB2", "Bo Hoa Hong Phan", 400000, "Hoa bo", "HOT"));
        data.add(new Flower("HB3", "Bo Hoa Cam Chuong", 220000, "Hoa bo", ""));

        return data;
    }

    // --- 3. MAIN FLOW ---
    public static void main(String[] args) {
        userList.add(new User("nhatthanh", "123456", "0399341361")); // User demo cho nhanh
        while (true) {
            System.out.println("\n========== FLOWER SHOP 4AE ==========");
            System.out.println("[1] Dang nhap | [2] Dang ky | [0] Thoat");
            System.out.print("Chon: ");
            String choice = sc.nextLine();
            if (choice.equals("1")) { if (processLogin()) showMainMenu(); }
            else if (choice.equals("2")) processRegister();
            else break;
        }
    }

    static void showMainMenu() {
        while (true) {
            System.out.println("\n--- TRANG CHU ---");
            System.out.println("[1] Xem cac KE HOA | [2] Tim kiem san pham | [3] Gio hang & Thanh toan | [0] Dang xuat");
            System.out.print("Chon: ");
            String c = sc.nextLine();
            if (c.equals("1")) shoppingFlow();
            else if (c.equals("2")) searchProduct();
            else if (c.equals("3")) checkoutFlow();
            else break;
        }
    }

    // --- TINH NANG MOI: TIM KIEM THEO TEN ---
    static void searchProduct() {
        ArrayList<Flower> all = initProductData();
        System.out.print("\nNhap ten hoa can tim: ");
        String key = sc.nextLine().toLowerCase();
        System.out.println("--- KET QUA TIM KIEM ---");
        for (Flower f : all) {
            if (f.name.toLowerCase().contains(key)) {
                System.out.println("[" + f.id + "] " + f.name + " | " + f.price + "d");
            }
        }
    }

    static void shoppingFlow() {
        ArrayList<Flower> allData = initProductData();
        while (true) {
            System.out.println("\n--- DANH MUC KE HANG ---");
            System.out.println("[1] Hoa chuc mung | [2] Hoa sinh nhat | [3] Chia buon | [4] Hoa bo | [0] Quay lai");
            System.out.print("Chon ke: ");
            String catChoice = sc.nextLine();
            if (catChoice.equals("0")) break;

            String targetCat = switch (catChoice) {
                case "1" -> "Hoa chuc mung";
                case "2" -> "Hoa sinh nhat";
                case "3" -> "Chia buon";
                case "4" -> "Hoa bo";
                default -> "";
            };

            if (targetCat.isEmpty()) continue;

            ArrayList<Flower> filtered = new ArrayList<>();
            for (Flower f : allData) if (f.category.equals(targetCat)) filtered.add(f);

            subShoppingMenu(filtered, targetCat);
        }
    }

    static void subShoppingMenu(ArrayList<Flower> list, String catName) {
        while (true) {
            System.out.println("\n--- KE: " + catName.toUpperCase() + " ---");
            for (Flower f : list) {
                String label = f.tag.isEmpty() ? "" : " [" + f.tag + "]";
                System.out.println(f.id + " | " + f.name + " | " + f.price + "d" + label);
            }
            System.out.println("\n[1] Gia tang | [2] Gia giam | [3] Loc san pham HOT | [4] CHON MUA | [0] Quay lai");
            System.out.print("Chon: ");
            String opt = sc.nextLine();

            if (opt.equals("1")) list.sort((f1, f2) -> Double.compare(f1.price, f2.price));
            else if (opt.equals("2")) list.sort((f1, f2) -> Double.compare(f2.price, f1.price));
            else if (opt.equals("3")) {
                System.out.println("--- CHI HIEN THI SAN PHAM HOT ---");
                for (Flower f : list) if (f.tag.equals("HOT")) System.out.println(f.id + " | " + f.name);
            } else if (opt.equals("4")) {
                System.out.print("Nhap ma hoa: "); String id = sc.nextLine();
                System.out.print("So luong: "); int q = Integer.parseInt(sc.nextLine());
                System.out.print("Loi nhan thiep/bang ron: "); String msg = sc.nextLine();
                for (Flower f : list) if (f.id.equalsIgnoreCase(id)) tempCart.add(new CartItem(f, q, msg));
            } else break;
        }
    }

    // --- LOGIC THANH TOAN CO VOUCHER ---
    static void checkoutFlow() {
        if (tempCart.isEmpty()) { System.out.println("Gio hang trong!"); return; }
        double total = 0;
        for (CartItem item : tempCart) total += item.flower.price * item.qty;

        System.out.print("\nBan co ma giam gia khong? (Nhap ma hoac bo qua): ");
        String voucher = sc.nextLine();
        double discount = 0;
        if (voucher.equalsIgnoreCase("4AE_FREE")) {
            discount = 50000; // Giam 50k
            System.out.println(">> Da ap dung ma 4AE_FREE: Giam 50,000 VND");
        }

        System.out.println("[1] Tai quay | [2] Giao hang (+35k ship)");
        String type = sc.nextLine();
        double finalPrice = total - discount + (type.equals("2") ? 35000 : 0);

        printInvoice(finalPrice, type.equals("2") ? "Giao hang tan noi" : "Nhan tai quay");
        tempCart.clear();
    }

    static void printInvoice(double total, String info) {
        System.out.println("\n*********************************************");
        System.out.println("            HOA DON THANH TOAN               ");
        System.out.println("Hinh thuc: " + info);
        System.out.println("TONG TIEN: " + total + " VND");
        System.out.println("*********************************************");
    }

    static void processRegister() {
        System.out.print("User: "); String u = sc.nextLine();
        System.out.print("Pass: "); String p = sc.nextLine();
        System.out.print("Phone: "); String ph = sc.nextLine();
        userList.add(new User(u, p, ph));
        System.out.println("Dang ky OK!");
    }

    static boolean processLogin() {
        System.out.print("User: "); String u = sc.nextLine();
        System.out.print("Pass: "); String p = sc.nextLine();
        for (User user : userList) if (user.u.equals(u) && user.p.equals(p)) { currentUser = user; return true; }
        return false;
    }
}
