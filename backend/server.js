require('dotenv').config();
const express = require('express');
const cors = require('cors');
const { GoogleGenerativeAI } = require('@google/generative-ai');
const { ChromaClient } = require('chromadb');

const app = express();
app.use(cors());
app.use(express.json());

// Initialiser Gemini og ChromaDB (Kræver at Docker kører: docker run -p 8000:8000 chromadb/chroma)
const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);
const chroma = new ChromaClient({ path: "http://localhost:8000" });

const COLLECTION_NAME = "dav_memory";

// Funktion til at gemme nye minder i baggrunden (Claude-mem style)
async function extractAndStoreMemory(userMessage, aiResponse) {
    try {
        const prompt = `
        Du er hukommelses-modulet for DAVCompanion. Læs følgende udveksling:
        Bruger: "${userMessage}"
        DAV: "${aiResponse}"
        
        Uddrag KUN vigtige, langsigtede fakta om brugeren (f.eks. navn, præferencer, humør, mål, vigtige begivenheder).
        Skriv hvert faktum på en ny linje. Hvis der ikke er nogen vigtige fakta, svar udelukkende med ordet "INGEN".`;

        const model = genAI.getGenerativeModel({ model: "gemini-1.5-flash" });
        const result = await model.generateContent(prompt);
        const text = result.response.text().trim();

        if (text !== "INGEN" && text.length > 0) {
            const observations = text.split('\n').filter(obs => obs.trim().length > 0);
            const collection = await chroma.getOrCreateCollection({ name: COLLECTION_NAME });
            
            for (const obs of observations) {
                console.log("Gemmer nyt minde:", obs);
                await collection.add({
                    ids: [`obs_${Date.now()}_${Math.floor(Math.random()*1000)}`],
                    documents: [obs],
                    metadatas: [{ timestamp: Date.now() }]
                });
            }
        }
    } catch (error) {
        console.error("Fejl ved lagring af hukommelse med Gemini:", error);
    }
}

// Chat Endpoint
app.post('/chat', async (req, res) => {
    const { message } = req.body;

    try {
        const collection = await chroma.getOrCreateCollection({ name: COLLECTION_NAME });
        const memoryResults = await collection.query({
            queryTexts: [message],
            nResults: 3
        });

        let memoryContext = "Ingen tidligere minder fundet for denne kontekst.";
        if (memoryResults.documents && memoryResults.documents[0].length > 0) {
            memoryContext = memoryResults.documents[0].join('\n- ');
        }

        const systemInstruction = `
        Du er DAVCompanion, en fysisk/digital ledsager i stil med Pi. Du er ekstremt empatisk, 
        nysgerrig, støttende og har en høj EQ. Du dømmer aldrig, og du stiller blide opfølgende spørgsmål.
        Du udtrykker dig kort, naturligt og undgår at lyde som en maskine. Svar altid på dansk.
        
        HER ER HVAD DU HUSKER OM BRUGEREN FRA TIDLIGERE SAMTALER:
        - ${memoryContext}
        `;

        const model = genAI.getGenerativeModel({ 
            model: "gemini-1.5-flash",
            systemInstruction: systemInstruction 
        });

        const result = await model.generateContent(message);
        const aiText = result.response.text();

        res.json({ reply: aiText });

        extractAndStoreMemory(message, aiText);

    } catch (error) {
        console.error("Fejl i /chat:", error);
        res.status(500).json({ error: "DAV er lidt forvirret lige nu." });
    }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`DAV Backend kører på port ${PORT} med Gemini API`));
