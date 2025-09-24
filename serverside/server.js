require('dotenv').config(); // to load env vars from .env

const express = require('express');
const axios = require('axios');

const app = express();
app.use(express.json());

// --- FLUTTERWAVE PAYMENT ROUTE ---
app.post('/create-flutterwave-payment', async (req, res) => {
  const { amount, currency, tx_ref, customer } = req.body;
  try {
    const resp = await axios.post('https://api.flutterwave.com/v3/payments', {
      tx_ref,
      amount,
      currency,
      redirect_url: 'https://your-server.com/flutterwave-callback',
      customer,
    }, {
      headers: { Authorization: `Bearer ${process.env.FLUTTERWAVE_SECRET}` }
    });
    return res.json(resp.data);
  } catch (err) {
    return res.status(500).json({ error: err.message });
  }
});

// --- SAFARICOM DARAJA HELPERS + ROUTES ---
async function getDarajaToken() {
  const credentials = Buffer.from(
    `${process.env.DARAJA_KEY}:${process.env.DARAJA_SECRET}`
  ).toString('base64');

  const resp = await axios.get(
    'https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials',
    { headers: { Authorization: `Basic ${credentials}` } }
  );

  return resp.data.access_token;
}

app.post('/stkpush', async (req, res) => {
  const { amount, phone } = req.body;
  try {
    const token = await getDarajaToken();
    const timestamp = new Date().toISOString().replace(/[^0-9]/g, '').slice(0, 14);
    const password = Buffer.from(
      process.env.BUSINESS_SHORTCODE + process.env.PASSKEY + timestamp
    ).toString('base64');

    const body = {
      BusinessShortCode: process.env.BUSINESS_SHORTCODE,
      Password: password,
      Timestamp: timestamp,
      TransactionType: "CustomerPayBillOnline",
      Amount: amount,
      PartyA: phone,
      PartyB: process.env.BUSINESS_SHORTCODE,
      PhoneNumber: phone,
      CallBackURL: "https://your-server.com/mpesa-callback",
      AccountReference: "POS Sale",
      TransactionDesc: "POS payment"
    };

    const resp = await axios.post(
      'https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest',
      body,
      { headers: { Authorization: `Bearer ${token}` } }
    );

    return res.json(resp.data);
  } catch (err) {
    return res.status(500).json({ error: err.message });
  }
});

// --- SERVER LISTENER ---
const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
